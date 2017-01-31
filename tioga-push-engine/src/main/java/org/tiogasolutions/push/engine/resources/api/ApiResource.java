/*
 * Copyright (c) 2014 Jacob D. Parr
 *
 * This software may not be used without permission.
 */
package org.tiogasolutions.push.engine.resources.api;

import org.tiogasolutions.push.engine.jaxrs.security.ApiAuthentication;
import org.tiogasolutions.push.engine.system.PubUtils;
import org.tiogasolutions.push.kernel.clients.DomainProfileEntity;
import org.tiogasolutions.push.kernel.execution.ExecutionManager;
import org.tiogasolutions.push.kernel.plugins.PushProcessor;
import org.tiogasolutions.push.pub.common.Push;
import org.tiogasolutions.push.pub.common.PushResponse;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.tiogasolutions.push.kernel.Paths.*;

@ApiAuthentication
public class ApiResource {

    private final PubUtils pubUtils;
    private final ExecutionManager executionManager;

    public ApiResource(ExecutionManager executionManager, PubUtils pubUtils) throws Exception {
        this.pubUtils = pubUtils;
        this.executionManager = executionManager;
    }

    @Path($domains)
    public DomainsResourceV3 getDomainResourceV3() {
        return new DomainsResourceV3(executionManager, pubUtils);
    }


    @POST
    @Path($callback)
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response callback(String msg) throws Exception {
        return Response.ok().build();
    }

    @POST
    @Path($pushes)
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postPush(Push push) throws Exception {

        DomainProfileEntity domain = executionManager.getContext().getDomain();
        PushProcessor pushProcessor = executionManager.getPushProcessor();

        PushResponse response = pushProcessor.execute(Push.CURRENT_API_VERSION, domain, push);
        return Response.ok(response, MediaType.APPLICATION_JSON).build();
    }

    @Path($config)
    public ConfigResource getConfigResource() {
        return new ConfigResource(executionManager, pubUtils);
    }
}
