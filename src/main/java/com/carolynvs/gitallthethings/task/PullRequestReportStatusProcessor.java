package com.carolynvs.gitallthethings.task;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.bamboo.build.BuildLoggerManager;
import com.atlassian.bamboo.build.CustomBuildProcessorServer;
import com.atlassian.bamboo.build.logger.BuildLogger;
import com.atlassian.bamboo.builder.BuildState;
import com.atlassian.bamboo.v2.build.BuildContext;
import com.atlassian.bamboo.v2.build.CurrentBuildResult;
import com.carolynvs.gitallthethings.BambooLinkBuilder;
import com.carolynvs.gitallthethings.PluginDataManager;
import com.carolynvs.gitallthethings.admin.GitThingsConfig;
import com.carolynvs.gitallthethings.github.GitHubCommitState;
import com.carolynvs.gitallthethings.github.GitHubCommunicator;
import com.carolynvs.gitallthethings.github.GitHubPullRequest;
import com.carolynvs.gitallthethings.github.GitHubSetCommitStatusRequest;
import com.carolynvs.gitallthethings.pullrequests.PullRequestBuildContext;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class PullRequestReportStatusProcessor implements CustomBuildProcessorServer {
    private final GitHubCommunicator github;
    private final BuildLoggerManager buildLoggerManager;
    private final PluginDataManager pluginData;
    private final BambooLinkBuilder bambooLinkBuilder;
    private BuildContext finalBuildContext;

    public PullRequestReportStatusProcessor(BuildLoggerManager buildLoggerManager, ActiveObjects ao) {
        this.buildLoggerManager = buildLoggerManager;
        this.github = new GitHubCommunicator();
        this.pluginData = new PluginDataManager(ao);
        this.bambooLinkBuilder = new BambooLinkBuilder();
    }

    @Override
    public void init(@NotNull BuildContext buildContext) {
        this.finalBuildContext = buildContext;
    }

    @NotNull
    @Override
    public BuildContext call() throws Exception {
        CurrentBuildResult finalBuildResult = finalBuildContext.getBuildResult();
        BuildState buildState = finalBuildResult.getBuildState();
        PullRequestBuildContext pullRequestBuildContext = new PullRequestBuildContext();
        BuildLogger logger = buildLoggerManager.getLogger(finalBuildContext.getResultKey());

        GitThingsConfig config = pluginData.getConfig(finalBuildContext.getPlanKey().toString());
        String token = config.getToken();
        String status = buildState == BuildState.SUCCESS ? GitHubCommitState.Success : GitHubCommitState.Failure;
        String description = buildState == BuildState.SUCCESS ? "The build succeeded." : "The build failed.";
        String buildResultUrl = bambooLinkBuilder.getBuildUrl(finalBuildContext.getParentBuildContext().getPlanResultKey().toString());

        GitHubPullRequest pullRequest = pullRequestBuildContext.getPullRequest(finalBuildContext, logger);
        if (pullRequest == null) {
            failBuild("Could not set pull request status because the pull request metadata could not be found.", finalBuildResult, logger, null);
            return finalBuildContext;
        }

        GitHubSetCommitStatusRequest statusRequest = new GitHubSetCommitStatusRequest(status, description, buildResultUrl);
        logger.addBuildLogEntry(String.format("Reporting a pull request status of %s for #%s to %s", pullRequest.Number, statusRequest.Status, pullRequest.StatusUrl));
        try {
            github.setPullRequestStatus(token, pullRequest, statusRequest);
        } catch (Exception ex) {
            failBuild("An error occurred when setting the pull request status", finalBuildResult, logger, ex);
        }

        return finalBuildContext;
    }

    private void failBuild(String errorMessage, CurrentBuildResult buildResult, BuildLogger logger, Exception ex) {
        logger.addErrorLogEntry(errorMessage, ex);
        buildResult.addBuildErrors(Arrays.asList(errorMessage));
        buildResult.setBuildState(BuildState.FAILED);
    }
}