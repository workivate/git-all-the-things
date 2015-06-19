package com.carolynvs.gitallthethings.task;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.bamboo.admin.configuration.AdministrationConfigurationService;
import com.atlassian.bamboo.task.RuntimeTaskDataProvider;
import com.atlassian.bamboo.task.TaskDefinition;
import com.atlassian.bamboo.task.runtime.RuntimeTaskDefinition;
import com.atlassian.bamboo.v2.build.BuildContext;
import com.atlassian.bamboo.v2.build.CommonContext;
import com.carolynvs.gitallthethings.admin.GitThingsConfig;
import com.carolynvs.gitallthethings.webhook.BambooLinkBuilder;
import com.carolynvs.gitallthethings.webhook.PluginDataManager;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class PullRequestReportStatusTaskDataProvider implements RuntimeTaskDataProvider
{
    public static final String TOKEN_KEY = "token";
    public static final String BUILD_RESULT_URL_KEY = "build_result_url";
    private final PluginDataManager pluginData;
    private final BambooLinkBuilder bambooLinkBuilder;

    public PullRequestReportStatusTaskDataProvider(ActiveObjects ao, AdministrationConfigurationService administrationConfigurationService)
    {
        this.pluginData = new PluginDataManager(ao);
        this.bambooLinkBuilder = new BambooLinkBuilder(administrationConfigurationService);
    }

    @NotNull
    @Override
    public Map<String, String> populateRuntimeTaskData(@NotNull TaskDefinition taskDefinition, @NotNull CommonContext commonContext)
    {
        Map<String, String> taskData = new HashMap<String, String>();

        BuildContext buildContext = (BuildContext)commonContext;
        GitThingsConfig config = pluginData.getConfig(buildContext.getPlanKey());

        taskData.put(TOKEN_KEY, config.getToken());
        taskData.put(BUILD_RESULT_URL_KEY, bambooLinkBuilder.getBuildUrl(buildContext.getParentBuildContext().getPlanResultKey().toString()));
        return taskData;
    }

    @Override
    public void processRuntimeTaskData(@NotNull TaskDefinition taskDefinition, @NotNull CommonContext commonContext) { }

    @Override
    public void processRuntimeTaskData(@NotNull RuntimeTaskDefinition runtimeTaskDefinition, @NotNull CommonContext commonContext) { }
}
