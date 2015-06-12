package com.carolynvs.github.webhook;

import com.atlassian.user.User;

public class PluginDataManager
{
    public String getWebHookSecret(String planKey)
    {
        // todo: store this either per repo or for all of bamboo
        return "";
    }

    public String getOAuthToken(String planKey)
    {
        // todo: store this either per repo or for all of bamboo
        return "";
    }

    public User getAssociatedUser(String planKey, PullRequest pullRequest)
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