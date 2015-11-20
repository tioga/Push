/*
 * Copyright (c) 2014 Jacob D. Parr
 *
 * This software may not be used without permission.
 */

package org.tiogasolutions.push.engine.resources.manage;

import org.tiogasolutions.dev.common.exceptions.ApiException;
import org.tiogasolutions.dev.common.exceptions.ExceptionUtils;
import org.tiogasolutions.dev.common.net.InetMediaType;
import org.tiogasolutions.push.engine.jaxrs.security.MngtAuthentication;
import org.tiogasolutions.push.engine.resources.manage.account.ManageAccountResource;
import org.tiogasolutions.push.engine.resources.manage.client.ManageDomainResource;
import org.tiogasolutions.push.kernel.accounts.AccountStore;
import org.tiogasolutions.push.kernel.accounts.DomainStore;
import org.tiogasolutions.push.kernel.actions.CreateDomainAction;
import org.tiogasolutions.push.kernel.clients.DomainProfileEntity;
import org.tiogasolutions.push.kernel.execution.ExecutionManager;
import org.tiogasolutions.push.kernel.plugins.Plugin;
import org.tiogasolutions.push.kernel.requests.PushRequestStore;
import org.tiogasolutions.push.kernel.system.PluginManager;
import org.tiogasolutions.push.kernel.system.SessionStore;
import org.tiogasolutions.push.pub.common.PushType;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;

@MngtAuthentication
public class ManageResource {

  private final UriInfo uriInfo;
  private final SessionStore sessionStore;
  private final PluginManager pluginManager;
  private final ExecutionManager executionManager;

  private final DomainStore domainStore;
  private final AccountStore accountStore;
  private final PushRequestStore pushRequestStore;

  public ManageResource(ExecutionManager executionManager, DomainStore domainStore, AccountStore accountStore, PushRequestStore pushRequestStore, SessionStore sessionStore, PluginManager pluginManager, UriInfo uriInfo) {
    this.uriInfo = uriInfo;
    this.sessionStore = sessionStore;
    this.pluginManager = pluginManager;
    this.executionManager = executionManager;
    this.domainStore = domainStore;
    this.accountStore = accountStore;
    this.pushRequestStore = pushRequestStore;
  }

  @GET
  public Response redirect() throws Exception {
    return Response.seeOther(new URI("manage/account")).build();
  }

  @GET
  @Produces(InetMediaType.IMAGE_PNG_VALUE)
  @Path("/{pushType}/icon-enabled")
  public Response getEnabledIcon(@PathParam("pushType") PushType pushType) throws Exception {

    Plugin plugin = pluginManager.getPlugin(pushType);
    byte[] bytes = plugin.getEnabledIcon();

    return Response.ok(bytes, InetMediaType.IMAGE_PNG_VALUE).build();
  }

  @GET
  @Produces(InetMediaType.IMAGE_PNG_VALUE)
  @Path("/{pushType}/icon-disabled")
  public Response getDisabledIcon(@PathParam("pushType") PushType pushType) throws Exception {

    Plugin plugin = pluginManager.getPlugin(pushType);
    byte[] bytes = plugin.getDisabledIcon();

    return Response.ok(bytes, InetMediaType.IMAGE_PNG_VALUE).build();
  }

  @Path("/account")
  public ManageAccountResource getManageAccountResource() {
    return new ManageAccountResource(executionManager, domainStore, accountStore, pluginManager, sessionStore);
  }

  @Path("/domain/{domainKey}")
  public ManageDomainResource getManageDomainResource(@PathParam("domainKey") String domainKey) throws Exception {
    return new ManageDomainResource(executionManager, domainStore, accountStore, pushRequestStore, pluginManager, domainKey);
  }

  @POST
  @Path("/domain")
  public Response createDomain(@FormParam("domainKey") String domainKey, @FormParam("domainPassword") String domainPassword) throws Exception {
    domainKey = ExceptionUtils.assertNotNull(domainKey, "domainKey").toLowerCase();

    if (domainStore.getByDomainKey(domainKey) != null) {
      throw ApiException.badRequest(String.format("The domain \"%s\" already exists.", domainKey));
    }

    CreateDomainAction action = new CreateDomainAction(executionManager.context().getAccount(), domainKey, domainPassword);

    DomainProfileEntity domain = executionManager.context().getAccount().add(action);
    domainStore.create(domain);
    accountStore.update(executionManager.context().getAccount());

    // Create a context for our new domain.
    executionManager.newContext(uriInfo).setDomain(domain);
    // Forces creation of domain-specific database.
    pushRequestStore.getDatabase();

    URI uri = uriInfo.getBaseUriBuilder().path("manage").path("domain").path(domainKey).build();
    return Response.seeOther(uri).build();
  }
}
