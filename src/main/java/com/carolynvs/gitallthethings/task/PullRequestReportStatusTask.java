package com.carolynvs.gitallthethings.task;

import com.atlassian.bamboo.admin.configuration.AdministrationConfigurationService;
import com.atlassian.bamboo.build.BuildLoggerManager;
import com.atlassian.bamboo.build.CustomBuildProcessorServer;
import com.atlassian.bamboo.build.logger.BuildLogger;
import com.atlassian.bamboo.builder.BuildState;
import com.atlassian.bamboo.task.*;
import com.atlassian.bamboo.v2.build.BuildContext;
import com.carolynvs.gitallthethings.PullRequestBuildContext;
import com.carolynvs.gitallthethings.webhook.*;
import org.jetbrains.annotations.NotNull;

public class PullRequestReportStatusTask implements TaskType, CustomBuildProcessorServer
{
    private final PluginDataManager pluginData;
    private final BambooLinkBuilder bambooLinkBuilder;
    private final GitHubCommunicator github;
    private final BuildLoggerManager buildLoggerManager;
    private BuildContext finalBuildContext;

    public PullRequestReportStatusTask(AdministrationConfigurationService administrationConfigurationService, BuildLoggerManager buildLoggerManager)
    {
        this.buildLoggerManager = buildLoggerManager;
        this.pluginData = new PluginDataManager();
        this.bambooLinkBuilder = new BambooLinkBuilder(administrationConfigurationService);
        this.github = new GitHubCommunicator();
    }

    @NotNull
    @Override
    public TaskResult execute(TaskContext taskContext)
            throws TaskException
    {
        // this task is simply a placeholder/indicator that the build status should be reported when the build completes
        TaskResultBuilder resultBuilder = TaskResultBuilder.newBuilder(taskContext);
        return resultBuilder.success().build();
    }

    @Override
    public void init(@NotNull BuildContext buildContext)
    {
        this.finalBuildContext = buildContext;
    }

    @NotNull
    @Override
    public BuildContext call()
            throws Exception
    {
        if(!shouldReportPullRequestStatus())
            return finalBuildContext;

        String planKey = finalBuildContext.getPlanKey();
        BuildState buildState = finalBuildContext.getBuildResult().getBuildState();
        PullRequestBuildContext pullRequestBuildContext = new PullRequestBuildContext();
        BuildLogger logger = buildLoggerManager.getLogger(finalBuildContext.getResultKey());

        String token = pluginData.getOAuthToken(planKey);
        String buildResultUrl = bambooLinkBuilder.getBuildUrl(finalBuildContext.getBuildResultKey());
        String status = buildState == BuildState.SUCCESS ? GitHubCommitState.Success : GitHubCommitState.Failure;
        String description = buildState == BuildState.SUCCESS ? "The build succeeded." : "The build failed.";
        GitHubSetCommitStatusRequest statusRequest = new GitHubSetCommitStatusRequest(status, description, buildResultUrl);

        PullRequest pullRequest = pullRequestBuildContext.getPullRequest(finalBuildContext, logger);
        if(pullRequest == null)
        {
            finalBuildContext.getBuildResult().setBuildReturnCode(1);
            return finalBuildContext;
        }

        logger.addBuildLogEntry(String.format("Reporting a pull request status of %s for #%s to %s", pullRequest.Number, statusRequest.Status, pullRequest.StatusUrl));
        try {
            github.setPullRequestStatus(token, pullRequest, statusRequest);
        } catch (Exception ex) {
            logger.addErrorLogEntry("Unable to set the pull request status", ex);
            finalBuildContext.getBuildResult().setBuildReturnCode(1);
        }

        return finalBuildContext;
    }

    private boolean shouldReportPullRequestStatus()
    {
        for (TaskDefinition taskDefinition : finalBuildContext.getBuildDefinition().getTaskDefinitions())
        {
            if(taskDefinition.getPluginKey().equals("com.carolynvs.gitallthethings:PullRequestReportStatusTask"))
                return true;
        }
        return false;
    }
}