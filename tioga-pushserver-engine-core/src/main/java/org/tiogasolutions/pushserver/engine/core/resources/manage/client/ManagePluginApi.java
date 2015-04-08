/*
 * Copyright (c) 2014 Jacob D. Parr
 *
 * This software may not be used without permission.
 */

package org.tiogasolutions.pushserver.engine.core.resources.manage.client;

import org.tiogasolutions.pushserver.engine.core.jaxrs.security.MngtAuthentication;
import org.tiogasolutions.pushserver.engine.core.system.CpApplication;
import org.tiogasolutions.pushserver.common.clients.Domain;
import org.tiogasolutions.pushserver.common.plugins.Plugin;
import org.tiogasolutions.pushserver.common.system.ExecutionContext;
import org.tiogasolutions.pushserver.common.system.PluginManager;
import org.tiogasolutions.pushserver.pub.common.PushType;
import org.tiogasolutions.dev.common.exceptions.ExceptionUtils;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.net.URI;

@MngtAuthentication
public class ManagePluginApi {

  private final PushType pushType;
  private final Domain domain;
  private final ExecutionContext context = CpApplication.getExecutionContext();

  public ManagePluginApi(Domain domain, PushType pushType) {
    this.pushType = ExceptionUtils.assertNotNull(pushType, "pushType");
    this.domain = ExceptionUtils.assertNotNull(domain, "domain");
  }

  public Response redirect() throws Exception {
    String path = String.format("manage/domain/%s", domain.getDomainKey());
    return Response.seeOther(new URI(path)).build();
  }

  @POST
  public Response updateConfig(MultivaluedMap<String, String> formParams) throws Exception {
    Plugin plugin = PluginManager.getPlugin(pushType);
    plugin.updateConfig(context, domain, formParams);
    return redirect();
  }

  @POST
  @Path("/delete")
  public Response deleteConfig() throws Exception {
    Plugin plugin = PluginManager.getPlugin(pushType);
    plugin.deleteConfig(context, domain);
    return redirect();
  }

  @POST
  @Path("/test")
  public Response testConfig() throws Exception {
    Plugin plugin = PluginManager.getPlugin(pushType);
    plugin.test(context, domain);
    return redirect();
  }
}
