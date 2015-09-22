package com.carolynvs.gitallthethings.pullrequests;

public class SetPullRequestStatusException extends Exception
{
    public SetPullRequestStatusException(String message, Exception cause)
    {
        super(message, cause);
    }
}
