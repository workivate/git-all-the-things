package com.carolynvs.gitallthethings.webhook;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.user.User;
import com.carolynvs.gitallthethings.admin.GitThingsConfig;

public class PluginDataManager
{
    private final ActiveObjects ao;

    public PluginDataManager(ActiveObjects ao)
    {
        this.ao = ao;
    }

    public String getWebHookSecret(final String planKey)
    {
        GitThingsConfig config = getConfig(planKey);

        return config.getSecret();
    }

    public String getOAuthToken(final String planKey)
    {
        GitThingsConfig config = getConfig(planKey);

        return config.getToken();
    }

    public GitThingsConfig getConfig(final String planKey)
    {
        final GitThingsConfig config = ao.executeInTransaction(new TransactionCallback<GitThingsConfig>() {
            @Override
            public GitThingsConfig doInTransaction() {
                GitThingsConfig[] rows = ao.find(GitThingsConfig.class, "plan_key = ?", planKey);
                GitThingsConfig config = rows.length > 0 ? rows[0] : ao.create(GitThingsConfig.class);
                return config;
            }
        });

        return config;
    }

    public void setConfig(final String token, final String secret, final String user, final String planKey)
    {
        ao.executeInTransaction(new TransactionCallback<GitThingsConfig>() {
            @Override
            public GitThingsConfig doInTransaction() {
                GitThingsConfig[] rows = ao.find(GitThingsConfig.class, "plan_key = ?", planKey);
                GitThingsConfig config = rows.length > 0 ? rows[0] : ao.create(GitThingsConfig.class);

                config.setToken(token);
                config.setSecret(secret);
                config.setPlanKey(planKey);
                config.setBotName(user);
                config.save();
                return config;
            }
        });
    }

    public User getAssociatedUser(String planKey, PullRequestEvent pullRequestEvent)
    {
        GitThingsConfig config = getConfig(planKey);

        return buildUser(config.getBotName());
    }

    private User buildUser(final String userName)
    {
        return new User() {
            @Override
            public String getFullName() {
                return userName;
            }

            @Override
            public String getEmail() {
                return "";
            }

            @Override
            public String getName() {
                return userName;
            }
        };
    }
}