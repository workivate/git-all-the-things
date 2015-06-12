package com.carolynvs.github.webhook;

import org.codehaus.jackson.annotate.JsonProperty;

public class PullRequest
{
    @JsonProperty("number")
    public Integer Number;

    @JsonProperty("user")
    public GitHubUser Owner;

    @JsonProperty("statuses_url")
    public String StatusUrl;
}
