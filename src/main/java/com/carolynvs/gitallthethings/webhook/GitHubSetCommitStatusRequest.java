package com.carolynvs.gitallthethings.webhook;

import org.codehaus.jackson.annotate.JsonProperty;

public class GitHubSetCommitStatusRequest
{
    @JsonProperty("state")
    public String Status;

    @JsonProperty("description")
    public String Description;

    @JsonProperty("target_url")
    public String BuildResultUrl;
}
