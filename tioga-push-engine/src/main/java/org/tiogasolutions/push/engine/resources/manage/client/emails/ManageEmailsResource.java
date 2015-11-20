/*
 * Copyright (c) 2014 Jacob D. Parr
 *
 * This software may not be used without permission.
 */
package org.tiogasolutions.push.engine.resources.manage.client.emails;

import org.tiogasolutions.push.engine.jaxrs.security.MngtAuthentication;
import org.tiogasolutions.push.engine.view.Thymeleaf;
import org.tiogasolutions.push.engine.view.ThymeleafViewFactory;
import org.tiogasolutions.push.kernel.accounts.Account;
import org.tiogasolutions.push.kernel.clients.DomainProfileEntity;
import org.tiogasolutions.push.kernel.execution.ExecutionManager;
import org.tiogasolutions.push.kernel.plugins.Plugin;
import org.tiogasolutions.push.kernel.requests.PushRequest;
import org.tiogasolutions.push.kernel.system.PluginManager;
import org.tiogasolutions.push.pub.SesEmailPush;
import org.tiogasolutions.push.pub.SmtpEmailPush;
import org.tiogasolutions.push.pub.common.CommonEmail;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@MngtAuthentication
public class ManageEmailsResource {

  private final Account account;
  private final DomainProfileEntity domainProfile;
  private final PluginManager pluginManager;
  private final ExecutionManager executionManager;

  public ManageEmailsResource(ExecutionManager executionManager, PluginManager pluginManager, Account account, DomainProfileEntity domainProfile) {
    this.pluginManager = pluginManager;
    this.executionManager = executionManager;
    this.account = account;
    this.domainProfile = domainProfile;
  }

  @GET
  @Produces(MediaType.TEXT_HTML)
  public Thymeleaf viewEmailEvents() throws Exception {
    List<PushRequest> requests = new ArrayList<>();
    requests.addAll(executionManager.context().getPushRequestStore().getByClientAndType(domainProfile, SesEmailPush.PUSH_TYPE));
    requests.addAll(executionManager.context().getPushRequestStore().getByClientAndType(domainProfile, SmtpEmailPush.PUSH_TYPE));

    EmailsModel model = new EmailsModel(account, domainProfile, requests);
    return new Thymeleaf(executionManager.context().getSession(), ThymeleafViewFactory.MANAGE_API_EMAILS, model);
  }

  @GET
  @Path("/{pushRequestId}")
  @Produces(MediaType.TEXT_HTML)
  public Thymeleaf viewEmailEvent(@PathParam("pushRequestId") String pushRequestId) throws Exception {

    PushRequest pushRequest = executionManager.context().getPushRequestStore().getByPushRequestId(pushRequestId);
    CommonEmail email = pushRequest.getCommonEmail();

    EmailModel model = new EmailModel(account, domainProfile, pushRequest, email);
    return new Thymeleaf(executionManager.context().getSession(), ThymeleafViewFactory.MANAGE_API_EMAIL, model);
  }

  @POST
  @Path("/{pushRequestId}/retry")
  public Response retryEmailMessage(@Context UriInfo uriInfo, @PathParam("pushRequestId") String pushRequestId) throws Exception {

    PushRequest pushRequest = executionManager.context().getPushRequestStore().getByPushRequestId(pushRequestId);
    CommonEmail push = (CommonEmail)pushRequest.getPush();

    if (SesEmailPush.PUSH_TYPE.equals(push.getPushType())) {
      Plugin plugin = pluginManager.getPlugin(push.getPushType());
      plugin.newDelegate(domainProfile, pushRequest, (SesEmailPush)push).retry();

    } else if (SmtpEmailPush.PUSH_TYPE.equals(push.getPushType())) {
      Plugin plugin = pluginManager.getPlugin(push.getPushType());
      plugin.newDelegate(domainProfile, pushRequest, (SmtpEmailPush)push).retry();

    } else {
      String msg = String.format("The retry operation is not supported for the push type \"%s\".", push.getPushType().getCode());
      throw new UnsupportedOperationException(msg);
    }

    URI uri = uriInfo.getBaseUriBuilder().path("manage").path("domain").path(domainProfile.getDomainKey()).path(pushRequest.getPushRequestId()).build();
    return Response.seeOther(uri).build();
  }
}
