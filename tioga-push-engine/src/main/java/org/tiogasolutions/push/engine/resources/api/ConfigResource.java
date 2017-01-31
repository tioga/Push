package org.tiogasolutions.push.engine.resources.api;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tiogasolutions.dev.common.net.HttpStatusCode;
import org.tiogasolutions.push.engine.jaxrs.security.ApiAuthentication;
import org.tiogasolutions.push.engine.system.PubUtils;
import org.tiogasolutions.push.kernel.clients.DomainProfileEntity;
import org.tiogasolutions.push.kernel.execution.ExecutionManager;
import org.tiogasolutions.push.kernel.plugins.Plugin;
import org.tiogasolutions.push.kernel.system.PluginManager;
import org.tiogasolutions.push.pub.common.PushType;
import org.tiogasolutions.push.pub.domain.PubConfig;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;

@ApiAuthentication
public class ConfigResource {

    public static final Log log = LogFactory.getLog(ConfigResource.class);

    private final ExecutionManager executionManager;
    private final PubUtils pubUtils;

    public ConfigResource(ExecutionManager executionManager, PubUtils pubUtils) {
        this.pubUtils = pubUtils;
        this.executionManager = executionManager;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getConfig() throws Exception {

        DomainProfileEntity domainProfile = executionManager.getContext().getDomain();
        PluginManager pluginManager = executionManager.getPluginManager();

        PubConfig config = pubUtils.toConfig(HttpStatusCode.OK, domainProfile, pluginManager);
        return pubUtils.toResponse(config).build();
    }

    @PUT
    @Path("{pushType}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response putConfig(@PathParam("pushType") PushType pushType, Map<String,String> params) throws Exception {
        log.info("Updating " + pushType);

        PluginManager pluginManager = executionManager.getPluginManager();
        DomainProfileEntity domainProfile = executionManager.getContext().getDomain();

        Plugin plugin = pluginManager.getPlugin(pushType);
        plugin.updateConfig(domainProfile, params);

        PubConfig config = pubUtils.toConfig(HttpStatusCode.OK, domainProfile, pluginManager);
        return pubUtils.toResponse(config).build();
    }

    @DELETE
    @Path("{pushType}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deleteConfig(@PathParam("pushType") PushType pushType) throws Exception {
        PluginManager pluginManager = executionManager.getPluginManager();
        Plugin plugin = pluginManager.getPlugin(pushType);
        DomainProfileEntity domainProfile = executionManager.getContext().getDomain();
        plugin.deleteConfig(domainProfile);

        PubConfig config = pubUtils.toConfig(HttpStatusCode.OK, domainProfile, pluginManager);
        return pubUtils.toResponse(config).build();
    }

    @POST
    @Path("{pushType}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response testConfig(@PathParam("pushType") PushType pushType) throws Exception {
        PluginManager pluginManager = executionManager.getPluginManager();
        Plugin plugin = pluginManager.getPlugin(pushType);
        DomainProfileEntity domainProfile = executionManager.getContext().getDomain();
        plugin.test(domainProfile);

        PubConfig config = pubUtils.toConfig(HttpStatusCode.OK, domainProfile, pluginManager);
        return pubUtils.toResponse(config).build();
    }
}
