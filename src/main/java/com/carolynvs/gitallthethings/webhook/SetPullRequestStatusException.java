package com.carolynvs.gitallthethings.webhook;

public class SetPullRequestStatusException extends Exception
{
    public SetPullRequestStatusException(String message, Exception cause)
    {
        super(message, cause);
    }
}
