/*
 * Copyright (c) 2014 Jacob D. Parr
 *
 * This software may not be used without permission.
 */
package org.tiogasolutions.push.engine.core.resources.manage.client;

import org.tiogasolutions.push.common.plugins.Plugin;
import org.tiogasolutions.push.common.system.ExecutionContext;
import org.tiogasolutions.push.engine.core.jaxrs.security.MngtAuthentication;
import org.tiogasolutions.push.engine.core.resources.manage.client.notifications.ManageNotificationsResource;
import org.tiogasolutions.push.engine.core.system.CpApplication;
import org.tiogasolutions.push.engine.core.view.Thymeleaf;
import org.tiogasolutions.push.engine.core.view.ThymeleafViewFactory;
import org.tiogasolutions.push.engine.core.resources.manage.client.emails.ManageEmailsResource;
import org.tiogasolutions.push.common.accounts.Account;
import org.tiogasolutions.push.common.accounts.DomainStore;
import org.tiogasolutions.push.common.actions.UpdateDomainAction;
import org.tiogasolutions.push.common.clients.Domain;
import org.tiogasolutions.push.common.requests.PushRequest;
import org.tiogasolutions.push.common.requests.PushRequestStore;
import org.tiogasolutions.push.common.requests.QueryResult;
import org.tiogasolutions.push.common.system.PluginManager;
import org.tiogasolutions.push.pub.common.PushType;
import org.tiogasolutions.dev.common.exceptions.ApiException;
import org.tiogasolutions.dev.common.net.InetMediaType;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@MngtAuthentication
public class ManageDomainResource {

  private final ExecutionContext execContext = CpApplication.getExecutionContext();
  private final String domainKey;
  private Domain _domain;

  public ManageDomainResource(String domainKey) {
    this.domainKey = domainKey;
  }

  private Account getAccount() {
    return execContext.getAccount();
  }

  private Domain getDomain() {
    if (_domain != null) {
      return _domain;
    }

    DomainStore domainStore = execContext.getDomainStore();
    _domain = domainStore.getByDomainKey(domainKey);
    execContext.setDomain(_domain);
    Account account = getAccount();

    if (_domain == null) {
      throw ApiException.notFound(domainKey);
    }
    return _domain;
  }

  @GET
  @Produces(MediaType.TEXT_HTML)
  public Thymeleaf viewDomain() throws Exception {
    Domain domain = getDomain();

    String lastMessage = execContext.getSession().getLastMessage();
    execContext.setLastMessage(null);
    execContext.getAccountStore().update(getAccount());

    ManageDomainModel model = new ManageDomainModel(execContext, getAccount(), domain, lastMessage);
    return new Thymeleaf(execContext.getSession(), ThymeleafViewFactory.MANAGE_API_CLIENT, model);
  }

  @GET
  @Path("/icon/{pushType}")
  @Produces(InetMediaType.IMAGE_PNG_VALUE)
  public Response getEnabledIcon(@PathParam("pushType") PushType pushType) throws Exception {

    Plugin plugin = PluginManager.getPlugin(pushType);
    byte[] bytes = plugin.getIcon(execContext, getDomain());

    return Response.ok(bytes, InetMediaType.IMAGE_PNG_VALUE).build();
  }

  @GET
  @Path("/requests")
  @Produces(MediaType.TEXT_HTML)
  public Thymeleaf viewEvents() throws Exception {
    Domain domain = getDomain();

    QueryResult<PushRequest> queryResult = execContext.getPushRequestStore().getByClient(domain, 500);
    List<PushRequest> requests = new ArrayList<>(queryResult.getEntityList());

    Collections.sort(requests);
    Collections.reverse(requests);

    DomainRequestsModel model = new DomainRequestsModel(getAccount(), domain, requests);
    return new Thymeleaf(execContext.getSession(), ThymeleafViewFactory.MANAGE_API_REQUESTS, model);
  }

  @POST
  @Path("/requests/delete-all")
  public Response deleteEvents() throws Exception {
    Domain domain = getDomain();
    PushRequestStore requestStore = execContext.getPushRequestStore();

    QueryResult<PushRequest> queryResult = requestStore.getByClient(domain, 500);
    List<PushRequest> requests = new ArrayList<>(queryResult.getEntityList());

    for (PushRequest request : requests) {
      requestStore.delete(request);
    }

    execContext.setLastMessage("All API Requests deleted");
    execContext.getDomainStore().update(domain);

    URI uri = execContext.getUriInfo().getBaseUriBuilder().path("manage").path("domain").path(domain.getDomainKey()).path("requests").build();
    return Response.seeOther(uri).build();
  }

  @Path("/emails")
  public ManageEmailsResource getManageEmailsResource() throws Exception {
    return new ManageEmailsResource(getAccount(), getDomain());
  }

  @Path("/notifications")
  public ManageNotificationsResource getManageNotificationsResource() throws Exception {
    return new ManageNotificationsResource(getAccount(), getDomain());
  }

  @POST
  public Response updateClient(@FormParam("domainKey") String domainKey, @FormParam("domainPassword") String domainPassword, @FormParam("retentionDays") int retentionDays) throws Exception {

    Domain domain = getDomain();
    DomainStore domainStore = execContext.getDomainStore();

    if (domain.getDomainKey().equals(domainKey) == false && domainStore.getByDomainKey(domainKey) != null) {
      // The specified name is not the same as the current value but it is already in use by another account.
      String msg = String.format("The client name %s already exists.", domainKey);
      throw ApiException.badRequest(msg);
    }

    UpdateDomainAction action = new UpdateDomainAction(domainKey, domainPassword, retentionDays);
    execContext.setLastMessage("Domain configuration changed.");

    domain.apply(action);
    domainStore.update(domain);

    URI uri = execContext.getUriInfo().getBaseUriBuilder().path("manage").path("domain").path(domain.getDomainKey()).build();
    return Response.seeOther(uri).build();
  }

  @POST
  @Path("/delete")
  public Response deleteClient() throws Exception {

    Domain domain = getDomain();
    execContext.getDomainStore().delete(domain);
    return Response.seeOther(new URI("manage/account")).build();
  }

  @Path("/{pushType}")
  public ManagePluginApi getManagePluginApi(@PathParam("pushType") PushType pushType) throws Exception {
    return new ManagePluginApi(getDomain(), pushType);
  }
}
