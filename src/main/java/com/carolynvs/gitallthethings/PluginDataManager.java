package com.carolynvs.gitallthethings;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.user.User;
import com.carolynvs.gitallthethings.admin.GitThingsConfig;
import com.carolynvs.gitallthethings.github.*;
import net.java.ao.EntityManager;
import net.java.ao.Query;
import net.java.ao.RawEntity;
import org.apache.commons.lang.StringUtils;

import java.beans.PropertyChangeListener;

public class PluginDataManager
{
    public static final String EMPTY_PLAN_KEY = "";
    private final ActiveObjects ao;

    public PluginDataManager(ActiveObjects ao)
    {
        this.ao = ao;
    }

    public GitThingsConfig getConfig(final String planKey)
    {
        final GitThingsConfig config = ao.executeInTransaction(new TransactionCallback<GitThingsConfig>() {
            @Override
            public GitThingsConfig doInTransaction() {
                GitThingsConfig[] rows = ao.find(GitThingsConfig.class,
                        Query.select()
                                .where("PLAN_KEY = ? OR PLAN_KEY = ?", planKey, EMPTY_PLAN_KEY)
                                .order("PLAN_KEY DESC"));

                GitThingsConfig config = rows.length > 0 ? rows[0] : new DefaultGitThingsConfig();

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
                GitThingsConfig[] rows = ao.find(GitThingsConfig.class, "PLAN_KEY = ?", planKey);
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

    public User getAssociatedUser(String planKey, GitHubPullRequestEvent pullRequestEvent)
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

            @Override
            public boolean isEnabled() {
                return true;
            }
        };
    }

    private class DefaultGitThingsConfig implements GitThingsConfig
    {

        @Override
        public String getPlanKey() {
            return null;
        }

        @Override
        public void setPlanKey(String planKey) {

        }

        @Override
        public String getToken() {
            return null;
        }

        @Override
        public void setToken(String token) {

        }

        @Override
        public String getSecret() {
            return null;
        }

        @Override
        public void setSecret(String secret) {

        }

        @Override
        public String getBotName() {
            return null;
        }

        @Override
        public void setBotName(String botName) {

        }

        @Override
        public int getID() {
            return 0;
        }

        @Override
        public void init() {

        }

        @Override
        public void save() {

        }

        @Override
        public EntityManager getEntityManager() {
            return null;
        }

        @Override
        public <X extends RawEntity<Integer>> Class<X> getEntityType() {
            return null;
        }


        @Override
        public void addPropertyChangeListener(PropertyChangeListener propertyChangeListener) {

        }

        @Override
        public void removePropertyChangeListener(PropertyChangeListener propertyChangeListener) {

        }
    }
}