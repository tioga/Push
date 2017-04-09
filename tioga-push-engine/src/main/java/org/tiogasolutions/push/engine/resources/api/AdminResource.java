package org.tiogasolutions.push.engine.resources.api;

import org.tiogasolutions.dev.common.net.HttpStatusCode;
import org.tiogasolutions.lib.hal.HalItem;
import org.tiogasolutions.lib.hal.HalLinks;
import org.tiogasolutions.lib.hal.HalLinksBuilder;
import org.tiogasolutions.push.engine.system.PubUtils;
import org.tiogasolutions.push.kernel.execution.ExecutionManager;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.tiogasolutions.push.kernel.Paths.$domains;

public class AdminResource {

    private final PubUtils pubUtils;
    private final ExecutionManager executionManager;

    public AdminResource(ExecutionManager executionManager, PubUtils pubUtils) {
        this.pubUtils = pubUtils;
        this.executionManager = executionManager;
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getLinks() {
        HalLinks links = HalLinksBuilder
                .builder()
                .add("self", pubUtils.newAdminLink())
                .add($domains, pubUtils.newAdminDomainsLink())
                .build();

        return pubUtils.toResponse(new HalItem(HttpStatusCode.OK, links)).build();
    }

    @Path($domains)
    public AdminDomainsResource getDomainResourceV3() {
        return new AdminDomainsResource(executionManager, pubUtils);
    }
}
