package com.carolynvs.gitallthethings.github;

import org.codehaus.jackson.annotate.JsonProperty;

public class GitHubSetCommitStatusRequest
{
    public GitHubSetCommitStatusRequest(String status, String description, String buildResultUrl)
    {
        this.Context = " continuous-integration/bamboo";
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

    @JsonProperty("context")
    public final String Context;
}
