package com.carolynvs.gitallthethings.task;

import com.atlassian.bamboo.collections.*;
import com.atlassian.bamboo.plugins.git.*;
import com.atlassian.bamboo.task.*;
import com.atlassian.bamboo.v2.build.agent.capability.*;
import com.google.common.collect.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class GitCheckoutTaskConfigurator extends AbstractTaskConfigurator implements TaskRequirementSupport
{
    public static final String REPO_PATH = "repoPath";

    @NotNull
    @Override
    public Map<String, String> generateTaskConfigMap(ActionParametersMap params, TaskDefinition previousTaskDefinition)
    {
        final Map<String, String> config = super.generateTaskConfigMap(params, previousTaskDefinition);

        config.put(REPO_PATH, params.getString(REPO_PATH));

        return config;
    }

    @Override
    public void populateContextForEdit(@NotNull Map<String, Object> context, @NotNull TaskDefinition taskDefinition)
    {
        super.populateContextForEdit(context, taskDefinition);
        context.put(REPO_PATH, taskDefinition.getConfiguration().get(REPO_PATH));
    }

    @Override
    public void populateContextForView(@NotNull Map<String, Object> context, @NotNull TaskDefinition taskDefinition)
    {
        super.populateContextForView(context, taskDefinition);
        context.put(REPO_PATH, taskDefinition.getConfiguration().get(REPO_PATH));
    }

    @NotNull
    @Override
    public Set<Requirement> calculateRequirements(@NotNull TaskDefinition taskDefinition)
    {
        Set<Requirement> requirements = Sets.newHashSet();
        requirements.add(new RequirementImpl(GitCapabilityTypeModule.GIT_CAPABILITY, true, ".*"));
        return requirements;
    }
}
