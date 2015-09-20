package com.carolynvs.gitallthethings.webhook;

import com.atlassian.bamboo.build.*;
import com.atlassian.bamboo.plan.*;
import com.atlassian.bamboo.plan.branch.*;
import com.atlassian.bamboo.plan.cache.*;
import com.atlassian.bamboo.variable.*;
import com.atlassian.user.*;
import com.carolynvs.gitallthethings.*;

import java.util.*;

public class PullRequestBuilder
{
    private final PlanTrigger planTrigger;
    private final PluginDataManager pluginData;
    private final GitHubCommunicator github;

    public PullRequestBuilder(BranchDetectionService branchDetectionService, CachedPlanManager cachedPlanManager, PlanManager planManager,
                              VariableConfigurationService variableConfigurationService,
                              PlanExecutionManager planExecutionManager, PluginDataManager pluginData,
                              GitHubCommunicator github, BambooLinkBuilder bambooLinkBuilder)
    {
        this.pluginData = pluginData;
        this.github = github;
        this.planTrigger = new PlanTrigger(branchDetectionService, cachedPlanManager, planManager, variableConfigurationService, planExecutionManager, bambooLinkBuilder);
    }

    public void build(String planKey, PullRequestEvent pullRequestEvent)
            throws PlanCreationDeniedException, SetPullRequestStatusException, Exception

    {
        PlanKey masterPlanKey = PlanKeys.getPlanKey(planKey);

        PlanKey branchPlanKey = ensureBranchPlanExists(masterPlanKey, pullRequestEvent.PullRequest);
        String buildResultUrl = triggerPlan(branchPlanKey, pullRequestEvent);
        setPullRequestStatusToPending(planKey, pullRequestEvent, buildResultUrl);
    }

    private void setPullRequestStatusToPending(String planKey, PullRequestEvent pullRequestEvent, String buildResultUrl)
            throws SetPullRequestStatusException

    {
        String token = pluginData.getConfig(planKey).getToken();
        GitHubSetCommitStatusRequest statusRequest = new GitHubSetCommitStatusRequest(GitHubCommitState.Pending, "The build is running", buildResultUrl);

        github.setPullRequestStatus(token, pullRequestEvent.PullRequest, statusRequest);
    }

    private String triggerPlan(PlanKey planKey, PullRequestEvent pullRequestEvent)
    {
        User triggerUser = pluginData.getAssociatedUser(planKey.toString(), pullRequestEvent);

        return planTrigger.execute(planKey, triggerUser);
    }

    public PlanKey ensureBranchPlanExists(PlanKey planKey, PullRequest pullRequest)
            throws PlanCreationDeniedException, Exception
    {
        return planTrigger.createPullRequestBranchPlan(planKey, pullRequest);
    }
}