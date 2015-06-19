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
    public String getWebHookSecret(String planKey)
    {
        // todo: store this either per repo or for all of bamboo
        return "6ba8caf8dc2b3951e8a4a278aea23a5e97bd6d59";
    }

    public String getOAuthToken(final String planKey)
    {
        final GitThingsConfig config = ao.executeInTransaction(new TransactionCallback<GitThingsConfig>() {
            @Override
            public GitThingsConfig doInTransaction() {
                GitThingsConfig[] rows = ao.find(GitThingsConfig.class, "plan_key = ?", planKey);
                GitThingsConfig config = rows.length > 0 ? rows[0] : ao.create(GitThingsConfig.class);
                return config;
            }
        });

        return config.getToken();
    }

    public void setOAuthToken(final String token, final String planKey)
    {
        ao.executeInTransaction(new TransactionCallback<GitThingsConfig>() {
            @Override
            public GitThingsConfig doInTransaction() {
                GitThingsConfig[] rows = ao.find(GitThingsConfig.class, "plan_key = ?", planKey);
                GitThingsConfig config = rows.length > 0 ? rows[0] : ao.create(GitThingsConfig.class);

                config.setToken(token);
                config.setPlanKey(planKey);
                config.save();
                return config;
            }
        });
    }

    public User getAssociatedUser(String planKey, PullRequestEvent pullRequestEvent)
    {
        // todo: let them pick a name per repo or for all of bamboo, or use the owner/sender
        return buildUser("GitHub");
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