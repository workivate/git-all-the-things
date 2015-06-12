package com.carolynvs.github.webhook;

import com.atlassian.bamboo.plan.PlanExecutionManager;
import com.atlassian.bamboo.plan.PlanKeys;
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

    public PullRequestTriggerResource(PlanManager planManager, PlanExecutionManager planExecutionManager)
    {
        this.pullRequestBuilder = new PullRequestBuilder(planManager, planExecutionManager);
    }

    @POST
    @Path("{plan-key}")
    public Response post(@PathParam("plan-key") String planKey, @HeaderParam("X_HUB_SIGNATURE") String signature, String jsonBody)
    {
        ObjectMapper mapper = new ObjectMapper();
        PullRequestEvent pullRequestEvent = null;
        try {
            pullRequestEvent = mapper.readValue(jsonBody, PullRequestEvent.class);
        }
        catch (IOException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        WebHookSecurity hookSecurity = new WebHookSecurity();
        if(!hookSecurity.validate(jsonBody, signature))
        {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        if(!PullRequestAction.OPENED.equals(pullRequestEvent.Action) &&
           !PullRequestAction.SYNCHRONIZE.equals(pullRequestEvent.Action))
        {
            return Response.status(Response.Status.ACCEPTED).build();
        }

        pullRequestBuilder.build(PlanKeys.getPlanKey(planKey), pullRequestEvent.Number);
        return Response.status(Response.Status.OK).build();
    }
}