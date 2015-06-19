package com.carolynvs.gitallthethings.admin;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.bamboo.configuration.GlobalAdminAction;
import com.atlassian.sal.api.transaction.TransactionCallback;

public class ConfigureGitAllTheThingsAction extends GlobalAdminAction
{
    private final ActiveObjects ao;

    private String token;

    public ConfigureGitAllTheThingsAction(ActiveObjects ao)
    {
        this.ao = ao;
    }

    @Override
    public String input()
            throws Exception
    {
        ao.executeInTransaction(new TransactionCallback<GitThingsConfig>() {
            @Override
            public GitThingsConfig doInTransaction()
            {
                GitThingsConfig[] rows = ao.find(GitThingsConfig.class, "plan_key is NULL");
                GitThingsConfig config = rows.length > 0 ? rows[0] : ao.create(GitThingsConfig.class);
                token = config.getToken();
                return null;
            }
        });

        return INPUT;
    }

    public String save()
            throws Exception
    {
        ao.executeInTransaction(new TransactionCallback<GitThingsConfig>() {
            @Override
            public GitThingsConfig doInTransaction() {
                GitThingsConfig[] rows = ao.find(GitThingsConfig.class, "plan_key is NULL");
                GitThingsConfig config = rows.length > 0 ? rows[0] : ao.create(GitThingsConfig.class);

                config.setToken(token);
                config.save();
                return config;
            }
        });
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
