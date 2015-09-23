package com.carolynvs.gitallthethings.task;

import com.atlassian.bamboo.collections.ActionParametersMap;
import com.atlassian.bamboo.plugins.git.GitCapabilityTypeModule;
import com.atlassian.bamboo.task.AbstractTaskConfigurator;
import com.atlassian.bamboo.task.TaskDefinition;
import com.atlassian.bamboo.task.TaskRequirementSupport;
import com.atlassian.bamboo.v2.build.agent.capability.Requirement;
import com.atlassian.bamboo.v2.build.agent.capability.RequirementImpl;
import com.google.common.collect.Sets;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;

public class PullRequestCheckoutTaskConfigurator extends GitCheckoutTaskConfigurator
{

}

