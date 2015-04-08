/*
 * Copyright (c) 2014 Jacob D. Parr
 *
 * This software may not be used without permission.
 */

package org.tiogasolutions.pushserver.engine.core.resources.manage;

import org.tiogasolutions.pushserver.common.system.ExecutionContext;
import org.tiogasolutions.pushserver.engine.core.jaxrs.security.MngtAuthentication;
import org.tiogasolutions.pushserver.engine.core.resources.manage.client.ManageDomainResource;
import org.tiogasolutions.pushserver.engine.core.system.CpApplication;
import org.tiogasolutions.pushserver.engine.core.resources.manage.account.ManageAccountResource;
import org.tiogasolutions.pushserver.common.actions.CreateDomainAction;
import org.tiogasolutions.pushserver.common.clients.Domain;
import org.tiogasolutions.pushserver.common.plugins.Plugin;
import org.tiogasolutions.pushserver.common.system.PluginManager;
import org.tiogasolutions.pushserver.pub.common.PushType;
import org.tiogasolutions.dev.common.exceptions.ApiException;
import org.tiogasolutions.dev.common.net.InetMediaType;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.net.URI;

@MngtAuthentication
public class ManageResource {

  private final ExecutionContext execContext = CpApplication.getExecutionContext();

  public ManageResource() {
  }

  @GET
  public Response redirect() throws Exception {
    return Response.seeOther(new URI("manage/account")).build();
  }

  @GET
  @Produces(InetMediaType.IMAGE_PNG_VALUE)
  @Path("/{pushType}/icon-enabled")
  public Response getEnabledIcon(@PathParam("pushType") PushType pushType) throws Exception {

    Plugin plugin = PluginManager.getPlugin(pushType);
    byte[] bytes = plugin.getEnabledIcon();

    return Response.ok(bytes, InetMediaType.IMAGE_PNG_VALUE).build();
  }

  @GET
  @Produces(InetMediaType.IMAGE_PNG_VALUE)
  @Path("/{pushType}/icon-disabled")
  public Response getDisabledIcon(@PathParam("pushType") PushType pushType) throws Exception {

    Plugin plugin = PluginManager.getPlugin(pushType);
    byte[] bytes = plugin.getDisabledIcon();

    return Response.ok(bytes, InetMediaType.IMAGE_PNG_VALUE).build();
  }

  @Path("/account")
  public ManageAccountResource getManageAccountResource() {
    return new ManageAccountResource(execContext.getAccount());
  }

  @Path("/domain/{domainKey}")
  public ManageDomainResource getManageDomainResource(@PathParam("domainKey") String domainKey) throws Exception {
    return new ManageDomainResource(domainKey);
  }

  @POST
  @Path("/domain")
  public Response createDomain(@FormParam("domainKey") String domainKey, @FormParam("domainPassword") String domainPassword) throws Exception {

    if (execContext.getDomainStore().getByDomainKey(domainKey) != null) {
      throw ApiException.badRequest(String.format("The domain \"%s\" already exists.", domainKey));
    }

    CreateDomainAction action = new CreateDomainAction(execContext.getAccount(), domainKey, domainPassword);

    Domain domain = execContext.getAccount().add(action);
    execContext.getDomainStore().create(domain);
    execContext.getAccountStore().update(execContext.getAccount());

    return Response.seeOther(new URI("manage/domain/"+domain.getDomainKey())).build();
  }
}
