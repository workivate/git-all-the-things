package com.carolynvs.github.webhook;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PullRequestEvent
{
    @JsonProperty("action")
    public String Action;

    @JsonProperty("pull_request")
    public PullRequest PullRequest;

    @JsonProperty("sender")
    public GitHubUser Sender;
}
