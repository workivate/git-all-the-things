package com.carolynvs.github.webhook;

import com.atlassian.bamboo.plan.PlanExecutionManager;
import com.atlassian.bamboo.plan.PlanKey;
import com.atlassian.bamboo.plan.PlanManager;
import com.atlassian.bamboo.plan.cache.ImmutableChain;
import com.atlassian.user.User;

import java.util.HashMap;
import java.util.Map;

public class PlanTrigger
{
    private final PlanExecutionManager planExecutionManager;

    public PlanTrigger(PlanManager planManager, PlanExecutionManager planExecutionManager)
    {
        this.planExecutionManager = planExecutionManager;
    }

    public void execute(PlanKey planKey, Map<String, String> variables)
    {
        ImmutableChain plan = null;
        User user = new User() {
            @Override
            public String getFullName() {
                return "GitHub";
            }

            @Override
            public String getEmail() {
                return "bamboo@github.com";
            }

            @Override
            public String getName() {
                return "GitHub";
            }
        };
        Map<String, String> params = new HashMap<String, String>();

        //planExecutionManager.startManualExecution(plan, user, params, variables);
    }
}
