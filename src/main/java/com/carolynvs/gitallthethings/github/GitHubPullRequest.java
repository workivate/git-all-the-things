package com.carolynvs.gitallthethings.github;

import org.codehaus.jackson.annotate.*;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GitHubPullRequest
{
    @JsonProperty("number")
    public int Number;

    @JsonProperty("title")
    public String Title;

    @JsonProperty("user")
    public GitHubUser Owner;

    @JsonProperty("statuses_url")
    public String StatusUrl;
}
