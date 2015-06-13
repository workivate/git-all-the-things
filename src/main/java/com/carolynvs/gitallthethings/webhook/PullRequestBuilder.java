package com.carolynvs.gitallthethings.webhook;

import com.atlassian.bamboo.plan.PlanExecutionManager;
import com.atlassian.bamboo.plan.PlanKeys;
import com.atlassian.bamboo.plan.PlanManager;
import com.atlassian.user.User;
import com.carolynvs.gitallthethings.PullRequestBuildContext;

import java.util.HashMap;
import java.util.Map;

public class PullRequestBuilder
{
    private final PlanTrigger planTrigger;
    private final PluginDataManager pluginData;
    private final GitHubCommunicator github;

    public PullRequestBuilder(PlanManager planManager, PlanExecutionManager planExecutionManager, PluginDataManager pluginData, GitHubCommunicator github, BambooLinkBuilder bambooLinkBuilder)
    {
        this.pluginData = pluginData;
        this.github = github;
        this.planTrigger = new PlanTrigger(planManager, planExecutionManager, bambooLinkBuilder);
    }

    public void build(String planKey, PullRequestEvent pullRequestEvent)
            throws Exception
    {
        PullRequestBuildContext buildContext = new PullRequestBuildContext();
        Map<String, String> variables = buildContext.createPullRequestVariables(pullRequestEvent.PullRequest);

        User triggerUser = pluginData.getAssociatedUser(planKey, pullRequestEvent);
        String buildResultUrl = planTrigger.execute(PlanKeys.getPlanKey(planKey), triggerUser, variables);

        String token = pluginData.getOAuthToken(planKey);
        GitHubSetCommitStatusRequest statusRequest = new GitHubSetCommitStatusRequest(GitHubCommitState.Pending, "The build is running", buildResultUrl);

        github.setPullRequestStatus(token, pullRequestEvent.PullRequest, statusRequest);
    }
}