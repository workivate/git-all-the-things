package com.carolynvs.github.webhook;

import com.atlassian.bamboo.chains.Chain;
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

    public PlanTrigger(PlanManager planManager, PlanExecutionManager planExecutionManager)
    {
        this.planManager = planManager;
        this.planExecutionManager = planExecutionManager;
    }

    public void execute(PlanKey planKey, User user, Map<String, String> variables)
    {
        ImmutableChain plan = planManager.getPlanByKey(planKey, Chain.class);

        Map<String, String> params = new HashMap<String, String>();

        planExecutionManager.startManualExecution(plan, user, params, variables);
    }
}