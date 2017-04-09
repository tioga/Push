package org.tiogasolutions.push.engine.resources.api;

import org.tiogasolutions.dev.common.exceptions.ApiException;
import org.tiogasolutions.dev.common.net.HttpStatusCode;
import org.tiogasolutions.lib.hal.HalItem;
import org.tiogasolutions.push.engine.system.PubUtils;
import org.tiogasolutions.push.kernel.clients.DomainProfileEntity;
import org.tiogasolutions.push.kernel.execution.ExecutionManager;
import org.tiogasolutions.push.pub.domain.PubDomainProfile;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

public class AdminDomainsResource {

    private final PubUtils pubUtils;
    private final ExecutionManager executionManager;

    public AdminDomainsResource(ExecutionManager executionManager, PubUtils pubUtils) {
        this.executionManager = executionManager;
        this.pubUtils = pubUtils;
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response getDomainProfiles() {
        List<DomainProfileEntity> entities = executionManager.getDomainStore().getAll();

        HalItem item = pubUtils.fromDomainProfileEntities(HttpStatusCode.OK, entities);
        return pubUtils.toResponse(item).build();
    }

    @GET
    @Path("{domainName}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getDomain(@PathParam("domainName") String domainName) {
        DomainProfileEntity domainProfileEntity = executionManager.getDomainStore().getByDomainName(domainName);
        if (domainProfileEntity == null) throw ApiException.notFound("The specified domain was not found.");

        PubDomainProfile pubDomainProfile = pubUtils.fromDomainProfileEntity(HttpStatusCode.OK, domainProfileEntity, true);
        return pubUtils.toResponse(pubDomainProfile).build();
    }
}
