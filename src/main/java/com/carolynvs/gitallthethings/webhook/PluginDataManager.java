package com.carolynvs.gitallthethings.webhook;

import com.atlassian.user.User;

public class PluginDataManager
{
    public String getWebHookSecret(String planKey)
    {
        // todo: store this either per repo or for all of bamboo
        return "6ba8caf8dc2b3951e8a4a278aea23a5e97bd6d59";
    }

    public String getOAuthToken(String planKey)
    {
        // todo: store this either per repo or for all of bamboo
        return "8f55c7e40a78f0cd5f5ec9364279968d8d46c2fb";
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