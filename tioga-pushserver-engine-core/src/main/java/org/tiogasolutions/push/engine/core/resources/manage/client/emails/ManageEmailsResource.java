/*
 * Copyright (c) 2014 Jacob D. Parr
 *
 * This software may not be used without permission.
 */
package org.tiogasolutions.push.engine.core.resources.manage.client.emails;

import org.tiogasolutions.push.common.system.ExecutionContext;
import org.tiogasolutions.push.engine.core.jaxrs.security.MngtAuthentication;
import org.tiogasolutions.push.engine.core.system.CpApplication;
import org.tiogasolutions.push.engine.core.view.Thymeleaf;
import org.tiogasolutions.push.engine.core.view.ThymeleafViewFactory;
import org.tiogasolutions.push.common.accounts.Account;
import org.tiogasolutions.push.common.clients.Domain;
import org.tiogasolutions.push.common.plugins.Plugin;
import org.tiogasolutions.push.common.requests.PushRequest;
import org.tiogasolutions.push.common.system.PluginManager;
import org.tiogasolutions.push.pub.EmailPush;
import org.tiogasolutions.push.pub.SesEmailPush;
import org.tiogasolutions.push.pub.SmtpEmailPush;

import javax.servlet.ServletContext;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@MngtAuthentication
public class ManageEmailsResource {

  private final Account account;
  private final Domain domain;
  private final ExecutionContext execContext = CpApplication.getExecutionContext();

  public ManageEmailsResource(Account account, Domain domain) {
    this.account = account;
    this.domain = domain;
  }

  @GET
  @Produces(MediaType.TEXT_HTML)
  public Thymeleaf viewEmailEvents() throws Exception {
    List<PushRequest> requests = new ArrayList<>();
    requests.addAll(execContext.getPushRequestStore().getByClientAndType(domain, EmailPush.PUSH_TYPE));
    requests.addAll(execContext.getPushRequestStore().getByClientAndType(domain, SesEmailPush.PUSH_TYPE));
    requests.addAll(execContext.getPushRequestStore().getByClientAndType(domain, SmtpEmailPush.PUSH_TYPE));

    EmailsModel model = new EmailsModel(account, domain, requests);
    return new Thymeleaf(execContext.getSession(), ThymeleafViewFactory.MANAGE_API_EMAILS, model);
  }

  @GET
  @Path("/{pushRequestId}")
  @Produces(MediaType.TEXT_HTML)
  public Thymeleaf viewEmailEvent(@PathParam("pushRequestId") String pushRequestId) throws Exception {

    PushRequest pushRequest = execContext.getPushRequestStore().getByPushRequestId(pushRequestId);
    EmailPush email = pushRequest.getEmailPush();

    EmailModel model = new EmailModel(account, domain, pushRequest, email);
    return new Thymeleaf(execContext.getSession(), ThymeleafViewFactory.MANAGE_API_EMAIL, model);
  }

  @POST
  @Path("/{pushRequestId}/retry")
  public Response retryEmailMessage(@Context ServletContext servletContext, @PathParam("pushRequestId") String pushRequestId) throws Exception {

    PushRequest pushRequest = execContext.getPushRequestStore().getByPushRequestId(pushRequestId);
    EmailPush push = (EmailPush)pushRequest.getPush();

    if (SesEmailPush.PUSH_TYPE.equals(push.getPushType())) {
      Plugin plugin = PluginManager.getPlugin(push.getPushType());
      plugin.newDelegate(execContext, domain, pushRequest, push).retry();

    } else if (SmtpEmailPush.PUSH_TYPE.equals(push.getPushType())) {
      Plugin plugin = PluginManager.getPlugin(push.getPushType());
      plugin.newDelegate(execContext, domain, pushRequest, push).retry();

    } else {
      String msg = String.format("The retry operation is not supported for the push type \"%s\".", push.getPushType().getCode());
      throw new UnsupportedOperationException(msg);
    }

    String path = String.format("%s/manage/domain/%s/emails/%s", servletContext.getContextPath(), domain.getDomainKey(), pushRequest.getPushRequestId());
    return Response.seeOther(new URI(path)).build();
  }
}
