package com.carolynvs.gitallthethings.github;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GitHubUser
{
    @JsonProperty("login")
    public String Login;
}
