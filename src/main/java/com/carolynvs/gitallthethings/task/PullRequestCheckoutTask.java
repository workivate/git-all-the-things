package com.carolynvs.gitallthethings.task;

import com.atlassian.bamboo.build.logger.BuildLogger;
import com.atlassian.bamboo.configuration.ConfigurationMap;
import com.atlassian.bamboo.plugins.git.GitCapabilityTypeModule;
import com.atlassian.bamboo.task.*;
import com.atlassian.bamboo.v2.build.agent.capability.CapabilityContext;
import com.atlassian.bamboo.variable.VariableDefinitionContext;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class PullRequestCheckoutTask implements TaskType
{
    private final CapabilityContext capabilityContext;

    public PullRequestCheckoutTask(CapabilityContext capabilityContext)
    {
        this.capabilityContext = capabilityContext;
    }

    @NotNull
    @Override
    public TaskResult execute(TaskContext taskContext)
            throws TaskException
    {
        TaskResultBuilder resultBuilder = TaskResultBuilder.create(taskContext);
        BuildLogger buildLogger = taskContext.getBuildLogger();

        PullRequestCheckoutTaskContext config = readConfiguration(taskContext, buildLogger);
        if(config == null)
            return resultBuilder.failed().build();

        boolean success = checkoutPullRequest(config);
        if(!success)
            return resultBuilder.failed().build();

        return resultBuilder.success().build();
    }

    private boolean checkoutPullRequest(PullRequestCheckoutTaskContext context)
            throws TaskException
    {
        boolean shouldClone = validateExistingRepository(context);
        if(shouldClone)
        {
            if(!context.Repository.exists())
                context.Repository.mkdirs();

            context.Logger.addBuildLogEntry(String.format("Cloning the default repository..."));
            GitCommandOutput cloneResult = context.Git.execute("clone", context.Remote, ".");
            if(!cloneResult.Succeeded)
                return false;
        }

        if(context.ShouldClean)
        {
            context.Logger.addBuildLogEntry("Cleaning...");
            GitCommandOutput cleanResult = context.Git.execute("clean", "-xdf");
            if(!cleanResult.Succeeded)
                return false;
        }

        context.Logger.addBuildLogEntry(String.format("Fetching Pull Request #%s...", context.PullRequest));
        GitCommandOutput fetchResult = context.Git.execute("fetch", "--update-head-ok", "origin", String.format("pull/%1$s/head:pull/%1$s", context.PullRequest));
        if(!fetchResult.Succeeded)
            return false;

        context.Logger.addBuildLogEntry(String.format("Checking out Pull Request #%s...", context.PullRequest));
        GitCommandOutput checkoutResult = context.Git.execute("checkout", String.format("pull/%s", context.PullRequest));
        if(!checkoutResult.Succeeded)
            return false;

        return true;
    }

    /* Prep the working directory for the clone, returning true if we should clone or false if we can skip the clone */
    private boolean validateExistingRepository(PullRequestCheckoutTaskContext context)
            throws TaskException
    {
        File gitDirectory = new File(context.Repository, ".git");
        if(!gitDirectory.exists())
            return true;

        GitCommandOutput remoteResult = context.Git.execute("remote", "show", "origin");
        if(remoteResult.Succeeded && remoteResult.Output.contains(context.Remote))
            return false; // We can skip clone, the repo has been cloned properly already

        context.Logger.addBuildLogEntry("Forcing a clean clone, the existing repository does not have the correct origin remote...");
        deleteDirectoryContents(context.Repository);
        return true;
    }

    private void deleteDirectoryContents(File rootDirectory)
            throws TaskException
    {
        for(File child : rootDirectory.listFiles())
        {
            if(child.isDirectory())
            {
                try {
                    FileUtils.deleteDirectory(child);
                } catch (IOException ex) {
                    throw new TaskException("Unable to force clean the working directory", ex);
                }
            }
            else
            {
                child.delete();
            }
        }
    }

    private PullRequestCheckoutTaskContext readConfiguration(TaskContext taskContext, BuildLogger logger)
    {
        ConfigurationMap taskConfig = taskContext.getConfigurationMap();
        String repoPath =  taskConfig.get(PullRequestCheckoutTaskConfigurator.REPO_PATH);
        File buildDirectory = taskContext.getWorkingDirectory();
        File repo = new File(buildDirectory, repoPath);

        Map<String, String> buildMetaData = taskContext.getBuildContext().getCurrentResult().getCustomBuildData();
        String remote = buildMetaData.get("planRepository.repositoryUrl");
        String revision = buildMetaData.get("planRepository.revision");

        Map<String, VariableDefinitionContext> buildVariables = taskContext.getBuildContext().getVariableContext().getEffectiveVariables();
        if(!buildVariables.containsKey("pullrequest"))
        {
            logger.addErrorLogEntry("The pullrequest variable is not set.");
            return null;
        }

        String pullRequestStr = buildVariables.get("pullrequest").getValue();
        if(StringUtils.isEmpty(pullRequestStr) || !StringUtils.isNumeric(pullRequestStr))
        {
            logger.addErrorLogEntry("The pullrequest variable must be set with an integer value.");
            return null;
        }
        Integer pullRequest = Integer.parseInt(pullRequestStr);

        return new PullRequestCheckoutTaskContext(logger, repo, remote, revision, pullRequest);
    }

    public String getGitExecutable()
    {
        return capabilityContext.getCapabilityValue(GitCapabilityTypeModule.GIT_CAPABILITY);
    }

    private class PullRequestCheckoutTaskContext
    {
        public PullRequestCheckoutTaskContext(BuildLogger logger, File repository, String remote, String revision, Integer pullRequest)
        {
            this.Logger = logger;
            this.Repository = repository;
            this.Remote = remote;
            this.Revision = revision;
            this.PullRequest = pullRequest;

            this.Git = new GitCommandRunner(getGitExecutable(), repository, logger);
            this.ShouldClean = true; // todo: make configurable
        }

        public final BuildLogger Logger;
        public final GitCommandRunner Git;
        public final File Repository;
        public final String Remote;
        public final String Revision;
        public final boolean ShouldClean;
        public final Integer PullRequest;
    }
}

