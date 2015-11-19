/*
 * Copyright (c) 2014 Jacob D. Parr
 *
 * This software may not be used without permission.
 */
package org.tiogasolutions.push.engine.core.resources.api;

import org.tiogasolutions.push.engine.core.jaxrs.security.ApiAuthentication;
import org.tiogasolutions.push.kernel.clients.DomainProfileEntity;
import org.tiogasolutions.push.kernel.execution.ExecutionManager;
import org.tiogasolutions.push.kernel.requests.PushRequest;
import org.tiogasolutions.push.pub.common.Push;
import org.tiogasolutions.push.pub.common.PushResponse;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@ApiAuthentication
public class ApiResourceV2 {

  private final ExecutionManager executionManager;

  public ApiResourceV2(ExecutionManager executionManager) throws Exception {
    this.executionManager = executionManager;
  }

  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("/callback")
  public Response callback(String msg) throws Exception {
    return Response.ok().build();
  }

  @POST
  @Path("/pushes")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response postPushV2(Push push) throws Exception {
    return postPush(push, Push.CURRENT_API_VERSION);
  }

  private Response postPush(Push push, int apiVersion) throws Exception {
    DomainProfileEntity domain = executionManager.context().getDomain();
    PushResponse response = executionManager.context().getPushProcessor().execute(apiVersion, domain, push);
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
