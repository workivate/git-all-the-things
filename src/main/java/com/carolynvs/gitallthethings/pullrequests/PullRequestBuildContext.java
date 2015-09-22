package com.carolynvs.gitallthethings.pullrequests;

import com.atlassian.bamboo.build.logger.BuildLogger;
import com.atlassian.bamboo.v2.build.BuildContext;
import com.atlassian.bamboo.variable.VariableDefinitionContext;
import com.carolynvs.gitallthethings.github.GitHubPullRequest;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class PullRequestBuildContext
{
    public static final String PULLREQUEST_NUMBER_VAR = "pullrequest.number";
    public static final String PULLREQUEST_STATUS_URL_VAR = "pullrequest.statusurl";

    public GitHubPullRequest getPullRequest(BuildContext buildContext, BuildLogger logger)
    {
        Map<String, VariableDefinitionContext> buildVars = buildContext.getVariableContext().getEffectiveVariables();

        if (!buildVars.containsKey(PULLREQUEST_NUMBER_VAR) || !buildVars.containsKey(PULLREQUEST_STATUS_URL_VAR)) {
            logger.addErrorLogEntry("The pullrequest variables are not set. If you are running a manual build, you must set the pullrequest.number and pullrequest.statusurl variables, i.e. run a customized build and override these variables with the desired pull request and the URL to which the status should be reported.");
            return null;
        }

        String number = buildVars.get(PULLREQUEST_NUMBER_VAR).getValue();
        if (StringUtils.isEmpty(number) || !StringUtils.isNumeric(number))

        {
            logger.addErrorLogEntry("The pullrequest.number variable must be set with an integer value.");
            return null;
        }

        String statusUrl = buildVars.get(PULLREQUEST_STATUS_URL_VAR).getValue();
        if(StringUtils.isEmpty(statusUrl))
        {
            logger.addErrorLogEntry("The pullrequest.statusurl variable must contain a URL.");
            return null;
        }

        GitHubPullRequest pullRequest = new GitHubPullRequest();
        pullRequest.Number = Integer.parseInt(number);
        pullRequest.StatusUrl = statusUrl;

        return pullRequest;
    }

    public Map<String, String> createPullRequestVariables(GitHubPullRequest pullRequest)
    {
        Map<String, String> variables = new HashMap<String, String>();
        variables.put(PULLREQUEST_NUMBER_VAR, Integer.toString(pullRequest.Number));
        variables.put(PULLREQUEST_STATUS_URL_VAR, pullRequest.StatusUrl);
        return variables;
    }
}
