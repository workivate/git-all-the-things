package com.carolynvs.gitallthethings.task;

import com.atlassian.bamboo.build.logger.*;
import com.carolynvs.gitallthethings.github.*;

import java.io.*;

public class PullRequestCheckoutTaskContext extends GitCheckoutTaskContext
{
    public PullRequestCheckoutTaskContext(BuildLogger logger, String gitExecutable, File repository, String remote, String revision, GitHubPullRequest pullRequest)
    {
        super(logger, gitExecutable, repository, remote, revision, buildRemoteRefName(pullRequest), buildBranchName(pullRequest));
        this.PullRequest = pullRequest;
    }

    public final GitHubPullRequest PullRequest;

    private static String buildRemoteRefName(GitHubPullRequest pullRequest)
    {
        return String.format("pull/%s/head", pullRequest.Number);
    }

    private static String buildBranchName(GitHubPullRequest pullRequest)
    {
        return String.format("pull/%s", pullRequest.Number);
    }
}
