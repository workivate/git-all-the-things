package com.carolynvs.github.webhook;

import com.atlassian.bamboo.plan.PlanExecutionManager;
import com.atlassian.bamboo.plan.PlanManager;
import org.codehaus.jackson.map.ObjectMapper;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

@Path("/pullrequest-trigger")
@Consumes({MediaType.APPLICATION_JSON})
public class PullRequestTriggerResource
{
    private final PullRequestBuilder pullRequestBuilder;
    private final GitHubCommunicator github;
    private final PluginDataManager pluginData;

    public PullRequestTriggerResource(PlanManager planManager, PlanExecutionManager planExecutionManager)
    {
        github = new GitHubCommunicator();
        pluginData = new PluginDataManager();
        pullRequestBuilder = new PullRequestBuilder(planManager, planExecutionManager, pluginData, github);
    }

    @POST
    @Path("{plan-key}")
    public Response post(@PathParam("plan-key") String planKey, @HeaderParam("X_HUB_SIGNATURE") String signature, String jsonBody)
    {
        PullRequestEvent pullRequestEvent = parsePullRequestEvent(jsonBody);
        if(pullRequestEvent == null)
            return Response.status(Response.Status.BAD_REQUEST).build();

        String webHookSecret = pluginData.getWebHookSecret(planKey);
        if(!github.validWebHook(webHookSecret, jsonBody, signature))
            return Response.status(Response.Status.UNAUTHORIZED).build();

        if(!isPullRequestContentChanged(pullRequestEvent))
            return Response.status(Response.Status.ACCEPTED).build();

        pullRequestBuilder.build(planKey, pullRequestEvent.PullRequest);
        return Response.status(Response.Status.OK).build();
    }

    private boolean isPullRequestContentChanged(PullRequestEvent pullRequestEvent)
    {
        return PullRequestAction.OPENED.equals(pullRequestEvent.Action) || PullRequestAction.SYNCHRONIZE.equals(pullRequestEvent.Action);
    }

    private PullRequestEvent parsePullRequestEvent(String jsonBody)
    {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(jsonBody, PullRequestEvent.class);
        }
        catch (IOException e) {
            return null;
        }
    }
}