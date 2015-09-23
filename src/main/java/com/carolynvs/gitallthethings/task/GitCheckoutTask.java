package com.carolynvs.gitallthethings.task;

import com.atlassian.bamboo.build.logger.*;
import com.atlassian.bamboo.build.logger.interceptors.*;
import com.atlassian.bamboo.configuration.*;
import com.atlassian.bamboo.plugins.git.*;
import com.atlassian.bamboo.task.*;
import com.atlassian.bamboo.v2.build.*;
import com.atlassian.bamboo.v2.build.agent.capability.*;
import org.apache.commons.io.*;
import org.jetbrains.annotations.*;

import java.io.*;
import java.util.*;

public class GitCheckoutTask implements TaskType
{
    private final CapabilityContext capabilityContext;

    public GitCheckoutTask(CapabilityContext capabilityContext)
    {
        this.capabilityContext = capabilityContext;
    }

    @NotNull
    @Override
    public TaskResult execute(@NotNull TaskContext taskContext)
            throws TaskException
    {
        TaskResultBuilder resultBuilder = TaskResultBuilder.newBuilder(taskContext);
        BuildLogger buildLogger = taskContext.getBuildLogger();
        CurrentResult currentResult = taskContext.getCommonContext().getCurrentResult();
        ErrorMemorisingInterceptor errorLines = ErrorMemorisingInterceptor.newInterceptor();
        buildLogger.getInterceptorStack().add(errorLines);

        GitCheckoutTaskContext config = readConfiguration(taskContext, buildLogger);
        if(config == null)
        {
            currentResult.addBuildErrors(errorLines.getErrorStringList());
            return resultBuilder.failed().build();
        }

        boolean success = checkoutBranch(config);
        if(!success)
        {
            currentResult.addBuildErrors(errorLines.getErrorStringList());
            return resultBuilder.failed().build();
        }

        return resultBuilder.success().build();
    }

    private boolean checkoutBranch(GitCheckoutTaskContext context)
            throws TaskException
    {
        boolean shouldClone = validateExistingRepository(context);
        if(shouldClone)
        {
            prepDirectoryForClone(context);

            context.Logger.addBuildLogEntry("Cloning the default repository...");
            GitCommandOutput cloneResult = context.Git.execute("clone", context.Remote, context.Repository.getAbsolutePath());
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

        if(branchExists(context.Git, context.BranchName))
        {
            // checkout detached head so we can safely delete
            context.Logger.addBuildLogEntry("Removing previous checkout of branch...");
            GitCommandOutput detachResult = context.Git.execute("checkout", "--detach");
            if(!detachResult.Succeeded)
                return false;

            // delete pull request branch
            GitCommandOutput deleteResult = context.Git.execute("branch", "-D", context.BranchName);
            if(!deleteResult.Succeeded)
                return false;
        }

        context.Logger.addBuildLogEntry("Fetching branch...");
        GitCommandOutput fetchResult = context.Git.execute("fetch", "origin", String.format("%s:%s", context.RemoteRefName, context.BranchName));
        if(!fetchResult.Succeeded)
            return false;

        context.Logger.addBuildLogEntry("Checking out branch...");
        GitCommandOutput checkoutResult = context.Git.execute("checkout", "--force", context.BranchName);
        if(!checkoutResult.Succeeded)
            return false;

        GitCommandOutput resetResult = context.Git.execute("reset", "--hard", context.Revision);
        if(!resetResult.Succeeded)
            return false;

        return true;
    }

    private boolean branchExists(final GitCommandRunner git, String branchName)
    {
        GitCommandOutput detachResult = git.execute("rev-parse", "--verify", branchName);
        return detachResult.Succeeded;
    }

    private void prepDirectoryForClone(GitCheckoutTaskContext context)
            throws TaskException
    {
        if(!context.Repository.isDirectory())
            context.Repository.delete();

        if(!context.Repository.exists())
            context.Repository.mkdirs();
        else if(context.Repository.list().length != 0)
            deleteDirectoryContents(context.Repository);
    }

    /* Prep the working directory for the clone, returning true if we should clone or false if we can skip the clone */
    private boolean validateExistingRepository(GitCheckoutTaskContext context)
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

    private void deleteDirectoryContents(@NotNull File rootDirectory)
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

    protected GitCheckoutTaskContext readConfiguration(TaskContext taskContext, BuildLogger logger)
    {
        ConfigurationMap taskConfig = taskContext.getConfigurationMap();
        String repoPath =  taskConfig.get(PullRequestCheckoutTaskConfigurator.REPO_PATH);
        File buildDirectory = taskContext.getWorkingDirectory();
        File repo = new File(buildDirectory, repoPath);

        Map<String, String> buildMetaData = taskContext.getBuildContext().getCurrentResult().getCustomBuildData();
        String remote = buildMetaData.get("planRepository.repositoryUrl");
        String revision = buildMetaData.get("planRepository.revision");
        String branchName = buildMetaData.get("planRepository.branch");
        String remoteRefName = branchName;

        String gitExecutable = getGitExecutable();

        return new GitCheckoutTaskContext(logger, gitExecutable, repo, remote, revision, remoteRefName, branchName);
    }

    public String getGitExecutable()
    {
        return capabilityContext.getCapabilityValue(GitCapabilityTypeModule.GIT_CAPABILITY);
    }
}
