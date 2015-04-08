/*
 * Copyright (c) 2014 Jacob D. Parr
 *
 * This software may not be used without permission.
 */
package org.tiogasolutions.pushserver.engine.core.resources.api;

import org.tiogasolutions.pushserver.engine.core.deprecated.NotificationPushV1;
import org.tiogasolutions.pushserver.engine.core.jaxrs.security.ApiAuthentication;
import org.tiogasolutions.pushserver.engine.core.resources.api.deprecated.NotificationDelegateV1;
import org.tiogasolutions.pushserver.engine.core.system.CpApplication;
import org.tiogasolutions.pushserver.common.clients.Domain;
import org.tiogasolutions.pushserver.common.requests.PushRequest;
import org.tiogasolutions.pushserver.common.system.AppContext;
import org.tiogasolutions.pushserver.common.system.ExecutionContext;
import org.tiogasolutions.pushserver.pub.common.Push;
import org.tiogasolutions.pushserver.pub.common.PushResponse;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@ApiAuthentication
public class ApiResourceV2 {

  private final ExecutionContext context = CpApplication.getExecutionContext();

  public ApiResourceV2() throws Exception {
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
    return postPush(push, AppContext.CURRENT_API_VERSION);
  }

  public Response postPushV1(Push push) throws Exception {
    return postPush(push, 1);
  }

  private Response postPush(Push push, int apiVersion) throws Exception {
    Domain domain = context.getDomain();

    if (push instanceof NotificationPushV1) {
      NotificationPushV1 notificationPushV1 = (NotificationPushV1)push;
      PushRequest pushRequest = new PushRequest(apiVersion, domain, push);
      context.getPushRequestStore().create(pushRequest);

      new NotificationDelegateV1(context, domain, pushRequest, notificationPushV1).start();
      return buildResponse(pushRequest, domain);
    }

    PushResponse response = context.getPushProcessor().execute(apiVersion, domain, push);
    return Response.ok(response, MediaType.APPLICATION_JSON).build();
  }

  private Response buildResponse(PushRequest pushRequest, Domain domain) throws Exception {
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
