package com.carolynvs.gitallthethings.task;

import com.atlassian.bamboo.build.logger.*;
import com.atlassian.bamboo.process.*;
import com.atlassian.utils.process.*;
import com.atlassian.utils.process.ExternalProcessBuilder;

import java.io.*;
import java.util.*;

public class GitCommandRunner
{
    private final String gitExecutable;
    private final File workingDirectory;
    private final BuildLogger buildLogger;

    public GitCommandRunner(String gitExecutable, File workingDirectory, BuildLogger buildLogger)
    {
        this.gitExecutable = gitExecutable;
        this.workingDirectory = workingDirectory;
        this.buildLogger = buildLogger;
    }

    public GitCommandOutput execute(String... commandArgs)
    {
        List<String> command = new ArrayList<String>();
        command.add(gitExecutable);
        command.addAll(Arrays.asList(commandArgs));

        StringOutputHandler outputHandler = new StringOutputHandler();
        ExternalProcess process = new ExternalProcessBuilder()
                .command(command, workingDirectory)
                .handler(new BambooProcessHandler(outputHandler, outputHandler))
                .build();
        buildLogger.addBuildLogEntry(String.format("Executing %s", process.getCommandLine()));
        process.execute();

        GitCommandOutput gitOutput = new GitCommandOutput(process.getHandler(), outputHandler);
        if(gitOutput.Succeeded)
            logInfo(gitOutput.Output);
        else
            logError(gitOutput.Output);

        return gitOutput;
    }

    private void logInfo(String message)
    {
        for(String line : message.split("\n"))
            buildLogger.addBuildLogEntry(line);
    }

    private void logError(String message)
    {
        for(String line : message.split("\n"))
            buildLogger.addErrorLogEntry(line);
    }
}

