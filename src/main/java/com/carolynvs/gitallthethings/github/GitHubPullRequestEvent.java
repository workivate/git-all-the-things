package com.carolynvs.gitallthethings.github;

import org.codehaus.jackson.annotate.*;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GitHubPullRequestEvent
{
    @JsonProperty("action")
    public String Action;

    @JsonProperty("pull_request")
    public GitHubPullRequest PullRequest;

    @JsonProperty("sender")
    public GitHubUser Sender;
}
