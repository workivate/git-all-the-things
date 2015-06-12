package com.carolynvs.github.webhook;

import org.codehaus.jackson.annotate.JsonProperty;

public class PullRequestEvent
{
    @JsonProperty("action")
    public String Action;

    @JsonProperty("pull_request")
    public PullRequest PullRequest;

    @JsonProperty("sender")
    public GitHubUser Sender;
}
