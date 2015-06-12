package com.carolynvs.github.webhook;

import com.atlassian.bamboo.plan.PlanExecutionManager;
import com.atlassian.bamboo.plan.PlanKeys;
import com.atlassian.bamboo.plan.PlanManager;
import com.atlassian.user.User;

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
        Map<String, String> variables = new HashMap<String, String>();
        variables.put("pullrequest", pullRequestEvent.PullRequest.Number.toString());

        User triggerUser = pluginData.getAssociatedUser(planKey, pullRequestEvent);
        String buildResultUrl = planTrigger.execute(PlanKeys.getPlanKey(planKey), triggerUser, variables);

        String token = pluginData.getOAuthToken(planKey);
        GitHubSetCommitStatusRequest statusRequest = new GitHubSetCommitStatusRequest();
        statusRequest.Status = GitHubCommitState.Pending;
        statusRequest.Description = "The build is running";
        statusRequest.BuildResultUrl = buildResultUrl;
        github.setPullRequestStatus(token, pullRequestEvent.PullRequest, statusRequest);
    }
}