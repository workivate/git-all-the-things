package com.carolynvs.github.webhook;

import org.codehaus.jackson.annotate.JsonProperty;

public class GitHubUser
{
    @JsonProperty("login")
    public String Login;
}
