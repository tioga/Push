/*
 * Copyright (c) 2014 Jacob D. Parr
 *
 * This software may not be used without permission.
 */
package org.tiogasolutions.push.engine.resources.api;

import org.tiogasolutions.push.engine.jaxrs.security.ApiAuthentication;
import org.tiogasolutions.push.engine.system.PubUtils;
import org.tiogasolutions.push.kernel.execution.ExecutionManager;

import javax.ws.rs.Path;

import static org.tiogasolutions.push.kernel.Paths.$client;
import static org.tiogasolutions.push.kernel.Paths.$domains;

@ApiAuthentication
public class ApiResourceV3 {

    private final PubUtils pubUtils;
    private final ExecutionManager executionManager;

    public ApiResourceV3(ExecutionManager executionManager, PubUtils pubUtils) throws Exception {
        this.pubUtils = pubUtils;
        this.executionManager = executionManager;
    }

    @Path($client)
    public ClientResourceV3 getClientResourceV3() {
        return new ClientResourceV3(executionManager);
    }

    @Path($domains)
    public DomainsResourceV3 getDomainResourceV3() {
        return new DomainsResourceV3(executionManager, pubUtils);
    }
}
