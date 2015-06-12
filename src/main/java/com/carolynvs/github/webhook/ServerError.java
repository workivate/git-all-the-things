package com.carolynvs.github.webhook;

import org.codehaus.jackson.annotate.JsonProperty;

public class ServerError
{
    public ServerError(Throwable exception)
    {
        Message = exception.getMessage();

        Throwable cause = exception.getCause();
        if(cause != null)
            Exception = cause.getMessage();
    }

    @JsonProperty("message")
    public String Message;

    @JsonProperty("exception")
    public String Exception;
}
