package com.carolynvs.gitallthethings.webhook;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.user.User;
import com.carolynvs.gitallthethings.admin.GitThingsConfig;
import net.java.ao.Query;
import org.apache.commons.lang.StringUtils;

public class PluginDataManager
{
    public static final String EMPTY_PLAN_KEY = "";
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
                GitThingsConfig[] rows = ao.find(GitThingsConfig.class,
                        Query.select()
                                .where("plan_key = ? OR plan_key = ?", planKey, EMPTY_PLAN_KEY)
                                .order("plan_key DESC"));

                GitThingsConfig config = rows.length > 0 ? rows[0] : ao.create(GitThingsConfig.class);

                defaultBotName(config);
                return config;
            }
        });

        return config;
    }

    private void defaultBotName(GitThingsConfig config)
    {
        if(config.getBotName() == null || StringUtils.isEmpty(config.getBotName()))
            config.setBotName("GitHub");
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

                defaultBotName(config);

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