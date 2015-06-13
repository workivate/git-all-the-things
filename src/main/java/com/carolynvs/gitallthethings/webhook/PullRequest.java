package com.carolynvs.gitallthethings.webhook;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PullRequest
{
    @JsonProperty("number")
    public Integer Number;

    @JsonProperty("user")
    public GitHubUser Owner;

    @JsonProperty("statuses_url")
    public String StatusUrl;
}
