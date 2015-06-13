package com.carolynvs.gitallthethings.webhook;

import com.atlassian.bamboo.chains.Chain;
import com.atlassian.bamboo.plan.ExecutionRequestResult;
import com.atlassian.bamboo.plan.PlanExecutionManager;
import com.atlassian.bamboo.plan.PlanKey;
import com.atlassian.bamboo.plan.PlanManager;
import com.atlassian.bamboo.plan.cache.ImmutableChain;
import com.atlassian.user.User;

import java.util.HashMap;
import java.util.Map;

public class PlanTrigger
{
    private final PlanManager planManager;
    private final PlanExecutionManager planExecutionManager;
    private final BambooLinkBuilder bambooLinkBuilder;

    public PlanTrigger(PlanManager planManager, PlanExecutionManager planExecutionManager, BambooLinkBuilder bambooLinkBuilder)
    {
        this.planManager = planManager;
        this.planExecutionManager = planExecutionManager;
        this.bambooLinkBuilder = bambooLinkBuilder;
    }

    public String execute(PlanKey planKey, User user, Map<String, String> variables)
    {
        ImmutableChain plan = planManager.getPlanByKey(planKey, Chain.class);

        Map<String, String> params = new HashMap<String, String>();

        ExecutionRequestResult result = planExecutionManager.startManualExecution(plan, user, params, variables);
        return bambooLinkBuilder.getBuildUrl(result.getPlanResultKey().toString());
    }
}