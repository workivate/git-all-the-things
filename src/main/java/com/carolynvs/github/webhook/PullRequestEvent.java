package com.carolynvs.github.webhook;

import org.codehaus.jackson.annotate.JsonProperty;

public class PullRequestEvent
{
    @JsonProperty("action")
    public String Action;

    @JsonProperty("number")
    public Integer Number;
}
