package com.carolynvs.gitallthethings.admin;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.bamboo.configuration.GlobalAdminAction;
import com.carolynvs.gitallthethings.webhook.PluginDataManager;

public class ConfigureGitAllTheThingsAction extends GlobalAdminAction
{
    private static final String GLOBAL = "";
    private final PluginDataManager pluginData;
    private String token;

    public ConfigureGitAllTheThingsAction(ActiveObjects ao)
    {
        this.pluginData = new PluginDataManager(ao);
    }

    @Override
    public String input()
            throws Exception
    {
        token = pluginData.getOAuthToken(GLOBAL);

        return INPUT;
    }

    public String save()
            throws Exception
    {
        pluginData.setOAuthToken(token, GLOBAL);
        return SUCCESS;
    }

    public String getToken()
    {
        return token;
    }

    public void setToken(String token)
    {
        this.token = token;
    }
}
