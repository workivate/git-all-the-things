package com.carolynvs.gitallthethings.task;

import com.atlassian.bamboo.build.logger.BuildLogger;
import com.atlassian.bamboo.build.logger.interceptors.ErrorMemorisingInterceptor;
import com.atlassian.bamboo.configuration.ConfigurationMap;
import com.atlassian.bamboo.plugins.git.GitCapabilityTypeModule;
import com.atlassian.bamboo.task.*;
import com.atlassian.bamboo.v2.build.CurrentResult;
import com.atlassian.bamboo.v2.build.agent.capability.CapabilityContext;
import com.carolynvs.gitallthethings.pullrequests.PullRequestBuildContext;
import com.carolynvs.gitallthethings.github.GitHubPullRequest;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class PullRequestCheckoutTask extends GitCheckoutTask
{
    public PullRequestCheckoutTask(CapabilityContext capabilityContext)
    {
        super(capabilityContext);
    }

    protected GitCheckoutTaskContext readConfiguration(TaskContext taskContext, BuildLogger logger)
    {
        PullRequestBuildContext pullRequestBuildContext = new PullRequestBuildContext();
        GitHubPullRequest pullRequest = pullRequestBuildContext.getPullRequest(taskContext.getBuildContext(), logger);
        if(pullRequest == null)
            return null;

        ConfigurationMap taskConfig = taskContext.getConfigurationMap();
        String repoPath =  taskConfig.get(PullRequestCheckoutTaskConfigurator.REPO_PATH);
        File buildDirectory = taskContext.getWorkingDirectory();
        File repo = new File(buildDirectory, repoPath);

        Map<String, String> buildMetaData = taskContext.getBuildContext().getCurrentResult().getCustomBuildData();
        String remote = buildMetaData.get("planRepository.repositoryUrl");
        String revision = buildMetaData.get("planRepository.revision");

        String gitExecutable = getGitExecutable();

        return new PullRequestCheckoutTaskContext(logger, gitExecutable, repo, remote, revision, pullRequest);
    }
}

