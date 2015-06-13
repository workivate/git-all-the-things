package com.carolynvs.gitallthethings.task;

import com.atlassian.utils.process.ProcessHandler;
import com.atlassian.utils.process.StringOutputHandler;

public class GitCommandOutput
{
    public boolean Succeeded;
    public String Output;

    public GitCommandOutput(ProcessHandler processHandler, StringOutputHandler outputHandler)
    {
        Succeeded = processHandler.succeeded();
        Output = outputHandler.getOutput().trim();
    }
}
