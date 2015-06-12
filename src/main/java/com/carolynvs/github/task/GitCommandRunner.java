package com.carolynvs.github.task;

import com.atlassian.bamboo.build.logger.BuildLogger;
import com.atlassian.bamboo.process.BambooProcessHandler;
import com.atlassian.utils.process.ExternalProcess;
import com.atlassian.utils.process.ExternalProcessBuilder;
import com.atlassian.utils.process.StringOutputHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
            buildLogger.addBuildLogEntry(gitOutput.Output);
        else
            buildLogger.addErrorLogEntry(gitOutput.Output);

        return gitOutput;
    }
}

