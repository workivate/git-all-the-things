package com.carolynvs.gitallthethings;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;

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

    public String toJson()
    {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (IOException e) {
            return "Unable to serialize ServerError. Check the server logs for the original exception.";
        }
    }
}
