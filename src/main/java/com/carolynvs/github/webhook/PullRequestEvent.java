package com.carolynvs.github.webhook;

import org.codehaus.jackson.annotate.JsonProperty;

public class PullRequestEvent
{
    @JsonProperty("action")
    public String Action;

    @JsonProperty("pull_request")
    public PullRequest PullRequest;

    @JsonProperty("statuses_url")
    public String StatusUrl;

    @JsonProperty("sender")
    public GitHubUser Sender;
}
