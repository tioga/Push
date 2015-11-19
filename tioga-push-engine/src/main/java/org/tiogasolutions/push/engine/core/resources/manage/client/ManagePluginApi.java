/*
 * Copyright (c) 2014 Jacob D. Parr
 *
 * This software may not be used without permission.
 */

package org.tiogasolutions.push.engine.core.resources.manage.client;

import org.tiogasolutions.dev.common.exceptions.ExceptionUtils;
import org.tiogasolutions.push.engine.core.jaxrs.security.MngtAuthentication;
import org.tiogasolutions.push.kernel.clients.DomainProfileEntity;
import org.tiogasolutions.push.kernel.execution.ExecutionManager;
import org.tiogasolutions.push.kernel.plugins.Plugin;
import org.tiogasolutions.push.kernel.system.PluginManager;
import org.tiogasolutions.push.pub.common.PushType;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.net.URI;

@MngtAuthentication
public class ManagePluginApi {

  private final PushType pushType;
  private final PluginManager pluginManager;
  private final DomainProfileEntity domainProfile;
  private final ExecutionManager executionManager;

  public ManagePluginApi(ExecutionManager executionManager,PluginManager pluginManager, DomainProfileEntity domainProfile, PushType pushType) {
    this.pluginManager = pluginManager;
    this.executionManager = executionManager;
    this.pushType = ExceptionUtils.assertNotNull(pushType, "pushType");
    this.domainProfile = ExceptionUtils.assertNotNull(domainProfile, "domainProfile");
  }

  public Response redirect() throws Exception {
    String path = String.format("manage/domain/%s", domainProfile.getDomainKey());
    return Response.seeOther(new URI(path)).build();
  }

  @POST
  public Response updateConfig(MultivaluedMap<String, String> formParams) throws Exception {
    Plugin plugin = pluginManager.getPlugin(pushType);
    plugin.updateConfig(domainProfile, formParams);
    return redirect();
  }

  @POST
  @Path("/delete")
  public Response deleteConfig() throws Exception {
    Plugin plugin = pluginManager.getPlugin(pushType);
    plugin.deleteConfig(domainProfile);
    return redirect();
  }

  @POST
  @Path("/test")
  public Response testConfig() throws Exception {
    Plugin plugin = pluginManager.getPlugin(pushType);
    plugin.test(domainProfile);
    return redirect();
  }
}
