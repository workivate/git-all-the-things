package com.carolynvs.gitallthethings.task;

import com.atlassian.bamboo.build.logger.*;

import java.io.*;

public class GitCheckoutTaskContext
{
    public GitCheckoutTaskContext(BuildLogger logger, String gitExecutable, File repository, String remote, String revision, String remoteRefName, String branchName)
    {
        this.ShouldClean = true; // todo: make configurable
        this.Logger = logger;
        this.Repository = repository;
        this.Remote = remote;
        this.Revision = revision;
        this.RemoteRefName = remoteRefName;
        this.BranchName = branchName;

        this.Git = new GitCommandRunner(gitExecutable, repository, logger);
    }

    public final BuildLogger Logger;
    public final GitCommandRunner Git;
    public final File Repository;
    public final String Remote;
    public final String Revision;
    public final String RemoteRefName;
    public final String BranchName;
    public final boolean ShouldClean;
}
