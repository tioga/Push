package org.tiogasolutions.push.engine.resources.api;

import org.tiogasolutions.push.kernel.clients.DomainProfileEntity;
import org.tiogasolutions.push.kernel.execution.ExecutionManager;
import org.tiogasolutions.push.kernel.plugins.PushProcessor;
import org.tiogasolutions.push.kernel.requests.PushRequest;
import org.tiogasolutions.push.pub.common.Push;
import org.tiogasolutions.push.pub.common.PushResponse;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.tiogasolutions.push.kernel.Paths.$callback;
import static org.tiogasolutions.push.kernel.Paths.$pushes;

public class ClientResourceV3 {

    private final ExecutionManager executionManager;

    public ClientResourceV3(ExecutionManager executionManager) {
        this.executionManager = executionManager;
    }

    @POST
    @Path($callback)
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response callback(String msg) throws Exception {
        return Response.ok().build();
    }

    @POST
    @Path($pushes)
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postPushV2(Push push) throws Exception {
        return postPush(push, Push.CURRENT_API_VERSION);
    }

    private Response postPush(Push push, int apiVersion) throws Exception {
        DomainProfileEntity domain = executionManager.getContext().getDomain();
        PushProcessor pushProcessor = executionManager.getPushProcessor();
        PushResponse response = pushProcessor.execute(apiVersion, domain, push);
        return Response.ok(response, MediaType.APPLICATION_JSON).build();
    }

    private Response buildResponse(PushRequest pushRequest, DomainProfileEntity domain) throws Exception {
        PushResponse response = new PushResponse(
                domain.getDomainId(),
                pushRequest.getPushRequestId(),
                pushRequest.getCreatedAt(),
                pushRequest.getRequestStatus(),
                pushRequest.getNotes()
        );
        return Response.ok(response, MediaType.APPLICATION_JSON).build();
    }
}
