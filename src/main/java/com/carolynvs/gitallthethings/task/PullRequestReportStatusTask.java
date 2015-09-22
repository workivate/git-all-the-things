package com.carolynvs.gitallthethings.task;

import com.atlassian.bamboo.build.BuildLoggerManager;
import com.atlassian.bamboo.build.CustomBuildProcessorServer;
import com.atlassian.bamboo.build.logger.BuildLogger;
import com.atlassian.bamboo.builder.BuildState;
import com.atlassian.bamboo.task.*;
import com.atlassian.bamboo.v2.build.BuildContext;
import com.carolynvs.gitallthethings.pullrequests.PullRequestBuildContext;
import com.carolynvs.gitallthethings.github.*;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class PullRequestReportStatusTask implements TaskType, CustomBuildProcessorServer
{
    private final GitHubCommunicator github;
    private final BuildLoggerManager buildLoggerManager;
    private BuildContext finalBuildContext;

    public PullRequestReportStatusTask(BuildLoggerManager buildLoggerManager)
    {
        this.buildLoggerManager = buildLoggerManager;
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
        TaskDefinition taskDefinition = getTaskDefinitionFromBuild();
        if(taskDefinition == null)
            return finalBuildContext;

        BuildState buildState = finalBuildContext.getBuildResult().getBuildState();
        PullRequestBuildContext pullRequestBuildContext = new PullRequestBuildContext();
        BuildLogger logger = buildLoggerManager.getLogger(finalBuildContext.getResultKey());

        final Map<String, String> taskData = finalBuildContext.getRuntimeTaskContext().getRuntimeContextForTask(taskDefinition);
        String token = taskData.get(PullRequestReportStatusTaskDataProvider.TOKEN_KEY);
        String buildResultUrl = taskData.get(PullRequestReportStatusTaskDataProvider.BUILD_RESULT_URL_KEY);
        String status = buildState == BuildState.SUCCESS ? GitHubCommitState.Success : GitHubCommitState.Failure;
        String description = buildState == BuildState.SUCCESS ? "The build succeeded." : "The build failed.";
        GitHubSetCommitStatusRequest statusRequest = new GitHubSetCommitStatusRequest(status, description, buildResultUrl);

        GitHubPullRequest pullRequest = pullRequestBuildContext.getPullRequest(finalBuildContext, logger);
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

    private TaskDefinition getTaskDefinitionFromBuild()
    {
        for (TaskDefinition taskDefinition : finalBuildContext.getBuildDefinition().getTaskDefinitions())
        {
            if(taskDefinition.getPluginKey().equals("com.carolynvs.gitallthethings:PullRequestReportStatusTask"))
                return taskDefinition;
        }
        return null;
    }
}