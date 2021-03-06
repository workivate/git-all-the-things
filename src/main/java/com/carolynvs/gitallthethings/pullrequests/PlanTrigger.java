package com.carolynvs.gitallthethings.pullrequests;

import com.atlassian.bamboo.build.*;
import com.atlassian.bamboo.build.creation.*;
import com.atlassian.bamboo.deletion.*;
import com.atlassian.bamboo.plan.*;
import com.atlassian.bamboo.plan.branch.*;
import com.atlassian.bamboo.plan.cache.*;
import com.atlassian.bamboo.security.*;
import com.atlassian.bamboo.utils.*;
import com.atlassian.bamboo.variable.*;
import com.atlassian.user.*;
import com.carolynvs.gitallthethings.*;
import com.carolynvs.gitallthethings.github.*;
import org.jetbrains.annotations.*;

import java.util.*;
import java.util.concurrent.*;

public class PlanTrigger
{
    private final BranchDetectionService branchDetectionService;
    private final CachedPlanManager cachedPlanManager;
    private final PlanManager planManager;
    private final VariableConfigurationService variableConfigurationService;
    private final DeletionService deletionService;
    private final PlanExecutionManager planExecutionManager;
    private final BambooLinkBuilder bambooLinkBuilder;

    public PlanTrigger(BranchDetectionService branchDetectionService, CachedPlanManager cachedPlanManager,
                       PlanManager planManager, VariableConfigurationService variableConfigurationService,
                       DeletionService deletionService,
                       PlanExecutionManager planExecutionManager, BambooLinkBuilder bambooLinkBuilder)
    {
        this.branchDetectionService = branchDetectionService;
        this.cachedPlanManager = cachedPlanManager;
        this.planManager = planManager;
        this.variableConfigurationService = variableConfigurationService;
        this.deletionService = deletionService;
        this.planExecutionManager = planExecutionManager;
        this.bambooLinkBuilder = bambooLinkBuilder;
    }

    public String execute(PlanKey planKey, User user)
    {
        ImmutableChain plan = (ImmutableChain)cachedPlanManager.getPlanByKey(planKey);

        ExecutionRequestResult result = planExecutionManager.startManualExecution(plan, user, Collections.EMPTY_MAP, Collections.EMPTY_MAP);
        return bambooLinkBuilder.getBuildUrl(result.getPlanResultKey().toString());
    }

    public void removeBranchPlan(PlanKey masterPlanKey, GitHubPullRequest pullRequest)
    {
        ImmutableChain masterPlan = cachedPlanManager.getMasterPlan(masterPlanKey);

        ImmutableChainBranch existingPlan = findBranchPlan(masterPlan, pullRequest.Number);
        if(existingPlan == null)
            return;

        removeBranchPlan(existingPlan.getPlanKey());
    }

    public PlanKey findOrCreateBranchPlan(PlanKey masterPlanKey, GitHubPullRequest pullRequest)
            throws PlanCreationDeniedException, Exception
    {
        ImmutableChain masterPlan = cachedPlanManager.getMasterPlan(masterPlanKey);

        ImmutableChainBranch existingPlan = findBranchPlan(masterPlan, pullRequest.Number);
        if(existingPlan != null)
            return existingPlan.getPlanKey();

        return createBranchPlanWithImpersonation(masterPlan, pullRequest);
    }

    private PlanKey createBranchPlanWithImpersonation(final ImmutableChain masterPlan, final GitHubPullRequest pullRequest)
            throws PlanCreationDeniedException, Exception
    {
        final BambooRunnables.BambooRunnableFromCallable<PlanKey> runnable = BambooRunnables.asBambooRunnable(new Callable<PlanKey>() {
            @Override
            public PlanKey call() throws PlanCreationDeniedException {
                return createBranchPlan(masterPlan, pullRequest);
            }
        });
        ImpersonationHelper.runWithSystemAuthority(runnable);
        return runnable.get();
    }

    private PlanKey createBranchPlan(final ImmutableChain masterPlan, final GitHubPullRequest pullRequest)
            throws PlanCreationDeniedException
    {
        String branchPlanName = String.format("Pull Request %s - %s", pullRequest.Number, pullRequest.Title);

        PlanKey branchPlanKey = branchDetectionService.createChainBranch(masterPlan, branchPlanName, null, null, PlanCreationService.EnablePlan.ENABLED, true);
        setPullRequestVariables(branchPlanKey, pullRequest);
        return branchPlanKey;
    }

    private void removeBranchPlan(PlanKey planKey)
    {
        deletionService.deletePlans(Arrays.asList(planKey.toString()));
    }

    private void setPullRequestVariables(PlanKey planKey, GitHubPullRequest pullRequest)
    {
        // Set the variables on the plan so that we can kick a build without custom variables
        Plan plan = planManager.getPlanByKey(planKey);

        PullRequestBuildContext pullRequestContext = new PullRequestBuildContext();
        Map<String, String> variables = pullRequestContext.createPullRequestVariables(pullRequest);
        for(Map.Entry<String, String> variable : variables.entrySet())
        {
            variableConfigurationService.createPlanVariable(plan, variable.getKey(), variable.getValue());
        }
    }

    @Nullable
    private ImmutableChainBranch findBranchPlan(ImmutableChain plan, Integer pullRequestNumber)
    {
        List<ImmutableChainBranch> branchPlans = cachedPlanManager.getBranchesForChain(plan);
        for(ImmutableChainBranch branchPlan : branchPlans)
        {
            List<VariableDefinition> planVariables = branchPlan.getVariables();
            for(VariableDefinition planVar : planVariables)
            {
                if(PullRequestBuildContext.PULLREQUEST_NUMBER_VAR.equals(planVar.getKey()) &&
                        pullRequestNumber.toString().equals(planVar.getValue()))
                {
                    return branchPlan;
                }
            }
        }

        return null;
    }
}