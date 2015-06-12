package com.carolynvs.github.webhook;

import com.atlassian.bamboo.admin.configuration.AdministrationConfigurationService;

public class BambooLinkBuilder
{
    private final String bambooUrl;

    // todo: find where Bamboo is building their links and use their code instead
    public BambooLinkBuilder(AdministrationConfigurationService administrationConfigurationService)
    {
        this.bambooUrl = administrationConfigurationService.getAdministrationConfiguration().getBaseUrl();
    }

    public String getBuildUrl(String buildKey)
    {
        return String.format("%s/browse/%s", bambooUrl, buildKey);
    }
}
