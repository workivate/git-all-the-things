package com.carolynvs.gitallthethings.webhook;

import org.codehaus.jackson.annotate.JsonProperty;

public class GitHubSetCommitStatusRequest
{
    public GitHubSetCommitStatusRequest(String status, String description, String buildResultUrl)
    {
        this.Status = status;
        this.Description = description;
        this.BuildResultUrl = buildResultUrl;
    }

    @JsonProperty("state")
    public final String Status;

    @JsonProperty("description")
    public final String Description;

    @JsonProperty("target_url")
    public final String BuildResultUrl;
}
