package com.carolynvs.gitallthethings.admin;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.bamboo.configuration.GlobalAdminAction;
import com.carolynvs.gitallthethings.PluginDataManager;

public class ConfigureGitAllTheThingsAction extends GlobalAdminAction
{
    private final PluginDataManager pluginData;
    private String token;
    private String secret;
    private String botName;

    public ConfigureGitAllTheThingsAction(ActiveObjects ao)
    {
        this.pluginData = new PluginDataManager(ao);
    }

    @Override
    public String input()
            throws Exception
    {
        GitThingsConfig config = pluginData.getConfig(PluginDataManager.EMPTY_PLAN_KEY);
        token = config.getToken();
        secret = config.getSecret();
        botName = config.getBotName();

        return INPUT;
    }

    public String save()
            throws Exception
    {
        pluginData.setConfig(token, secret, botName, PluginDataManager.EMPTY_PLAN_KEY);
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

    public String getSecret()
    {
        return secret;
    }

    public void setSecret(String secret)
    {
        this.secret = secret;
    }

    public String getBotName()
    {
        return botName;
    }

    public void setBotName(String botName)
    {
        this.botName = botName;
    }
}
