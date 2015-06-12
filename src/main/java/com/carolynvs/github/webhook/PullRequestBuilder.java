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

    public PullRequestBuilder(PlanManager planManager, PlanExecutionManager planExecutionManager, PluginDataManager pluginData, GitHubCommunicator github)
    {
        this.pluginData = pluginData;
        this.github = github;
        this.planTrigger = new PlanTrigger(planManager, planExecutionManager);
    }

    public void build(String planKey, PullRequest pullRequest)
    {
        Map<String, String> variables = new HashMap<String, String>();
        variables.put("pullrequest", pullRequest.Number.toString());

        User triggerUser = pluginData.getAssociatedUser(planKey, pullRequest);
        planTrigger.execute(PlanKeys.getPlanKey(planKey), triggerUser, variables);

        String token = pluginData.getOAuthToken(planKey);
        github.setPullRequestStatus(token, pullRequest.Number, "pending", "build is running");
    }
}