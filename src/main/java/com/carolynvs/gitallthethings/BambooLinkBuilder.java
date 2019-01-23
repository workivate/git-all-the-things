package com.carolynvs.gitallthethings;

import com.atlassian.bamboo.configuration.AdministrationConfiguration;
import com.atlassian.spring.container.ContainerManager;

public class BambooLinkBuilder {
    private final String bambooUrl;

    // todo: find where Bamboo is building their links and use their code instead
    public BambooLinkBuilder() {
        this.bambooUrl = ((AdministrationConfiguration) ContainerManager.getComponent("administrationConfiguration")).getBaseUrl();
    }

    public String getBuildUrl(String buildKey) {
        return String.format("%s/browse/%s", bambooUrl, buildKey);
    }
}
