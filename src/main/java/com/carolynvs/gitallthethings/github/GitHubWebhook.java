package com.carolynvs.gitallthethings.github;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.bamboo.admin.configuration.AdministrationConfigurationService;
import com.atlassian.bamboo.build.*;
import com.atlassian.bamboo.plan.*;
import com.atlassian.bamboo.plan.branch.*;
import com.atlassian.bamboo.plan.cache.*;
import com.atlassian.bamboo.variable.*;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.carolynvs.gitallthethings.*;
import com.carolynvs.gitallthethings.pullrequests.*;
import org.codehaus.jackson.map.ObjectMapper;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

@AnonymousAllowed
@Path("/pullrequest-trigger")
@Consumes({MediaType.APPLICATION_JSON})
public class GitHubWebhook
{
    private final PullRequestBuilder pullRequestBuilder;
    private final GitHubCommunicator github;
    private final PluginDataManager pluginData;

    public GitHubWebhook(BranchDetectionService branchDetectionService, CachedPlanManager cachedPlanManager, PlanManager planManager,
                         VariableConfigurationService variableConfigurationService,
                         PlanExecutionManager planExecutionManager, AdministrationConfigurationService administrationConfigurationService,
                         ActiveObjects ao)
    {
        this.github = new GitHubCommunicator();
        this.pluginData = new PluginDataManager(ao);
        BambooLinkBuilder bambooLinkBuilder = new BambooLinkBuilder(administrationConfigurationService);
        this.pullRequestBuilder = new PullRequestBuilder(branchDetectionService, cachedPlanManager, planManager, variableConfigurationService, planExecutionManager, pluginData, github, bambooLinkBuilder);

    }

    @POST
    @Path("{plan-key}")
    public Response post(@PathParam("plan-key") String planKey, @HeaderParam("X-GitHub-Event") String event, @HeaderParam("X-Hub-Signature") String signature, String jsonBody)
    {
        if(isPing(event))
            return Response.ok().build();

        GitHubPullRequestEvent pullRequestEvent = parsePullRequestEvent(jsonBody);
        if(pullRequestEvent == null)
            return Response.status(Response.Status.BAD_REQUEST).build();

        String webHookSecret = pluginData.getConfig(planKey).getSecret();
        if(!github.validWebHook(webHookSecret, jsonBody, signature))
            return Response.status(Response.Status.UNAUTHORIZED).build();

        if(!isPullRequestContentChanged(pullRequestEvent))
            return Response.status(Response.Status.ACCEPTED).build();

        try {
            pullRequestBuilder.build(planKey, pullRequestEvent);
        } catch (PlanCreationDeniedException e) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("You do not have permission to create a branch plan for the specified pull request").build();
        } catch(PlanCreationException e) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("You do not have permission to create a branch plan for the specified pull request").build();
        } catch (Exception e) {
            return Response.serverError().entity(new ServerError(e).toJson()).build();
        }


        return Response.status(Response.Status.OK).build();
    }

    private boolean isPing(String event)
    {
        return event.equals("ping");
    }

    private boolean isPullRequestContentChanged(GitHubPullRequestEvent pullRequestEvent)
    {
        return PullRequestAction.OPENED.equals(pullRequestEvent.Action) || PullRequestAction.SYNCHRONIZE.equals(pullRequestEvent.Action);
    }

    private GitHubPullRequestEvent parsePullRequestEvent(String jsonBody)
    {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(jsonBody, GitHubPullRequestEvent.class);
        }
        catch (IOException e) {
            return null;
        }
    }
}