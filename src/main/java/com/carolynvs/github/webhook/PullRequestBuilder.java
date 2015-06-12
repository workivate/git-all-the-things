package com.carolynvs.github.webhook;

import com.atlassian.bamboo.plan.PlanExecutionManager;
import com.atlassian.bamboo.plan.PlanKey;
import com.atlassian.bamboo.plan.PlanManager;

import java.util.HashMap;
import java.util.Map;

public class PullRequestBuilder
{
    private final PlanTrigger planTrigger;

    public PullRequestBuilder(PlanManager planManager, PlanExecutionManager planExecutionManager)
    {
        this.planTrigger = new PlanTrigger(planManager, planExecutionManager);
    }

    public void build(PlanKey planKey, Integer pullRequestId)
    {
        Map<String, String> variables = new HashMap<String, String>();
        variables.put("pullrequest", pullRequestId.toString());

        planTrigger.execute(planKey, variables);
    }
}
