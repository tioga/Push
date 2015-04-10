/*
 * Copyright (c) 2014 Jacob D. Parr
 *
 * This software may not be used without permission.
 */

package org.tiogasolutions.push.engine.core.resources.manage.client.notifications;

import org.tiogasolutions.push.common.system.ExecutionContext;
import org.tiogasolutions.push.engine.core.resources.manage.client.DomainRequestsModel;
import org.tiogasolutions.push.engine.core.jaxrs.security.MngtAuthentication;
import org.tiogasolutions.push.engine.core.system.CpApplication;
import org.tiogasolutions.push.engine.core.view.Thymeleaf;
import org.tiogasolutions.push.engine.core.view.ThymeleafViewFactory;
import org.tiogasolutions.push.common.accounts.Account;
import org.tiogasolutions.push.common.clients.Domain;
import org.tiogasolutions.push.common.requests.PushRequest;
import org.tiogasolutions.push.pub.LqNotificationPush;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@MngtAuthentication
public class ManageNotificationsResource {

  private final Account account;
  private final Domain domain;
  private final ExecutionContext execContext = CpApplication.getExecutionContext();

  public ManageNotificationsResource(Account account, Domain domain) {
    this.account = account;
    this.domain = domain;
  }

  @GET
  @Produces(MediaType.TEXT_HTML)
  public Thymeleaf viewNotifications() throws Exception {

    List<PushRequest> requests = new ArrayList<>();
    requests.addAll(execContext.getPushRequestStore().getByClientAndType(domain, LqNotificationPush.PUSH_TYPE));

    Collections.sort(requests);
    Collections.reverse(requests);

    DomainRequestsModel model = new DomainRequestsModel(account, domain, requests);
    return new Thymeleaf(execContext.getSession(), ThymeleafViewFactory.MANAGE_API_NOTIFICATIONS, model);
  }

  @GET
  @Path("/{pushRequestId}")
  @Produces(MediaType.TEXT_HTML)
  public Thymeleaf viewNotifications(@PathParam("pushRequestId") String pushRequestId) throws Exception {

    PushRequest request = execContext.getPushRequestStore().getByPushRequestId(pushRequestId);
    LqNotificationPush notification = request.getNotificationPush();

    DomainNotificationModel model = new DomainNotificationModel(account, domain, request, notification);
    return new Thymeleaf(execContext.getSession(), ThymeleafViewFactory.MANAGE_API_NOTIFICATION, model);
  }
}
