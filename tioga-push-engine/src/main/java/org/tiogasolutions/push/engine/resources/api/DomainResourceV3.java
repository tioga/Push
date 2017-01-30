package org.tiogasolutions.push.engine.resources.api;

import org.tiogasolutions.dev.common.exceptions.ApiException;
import org.tiogasolutions.dev.common.net.HttpStatusCode;
import org.tiogasolutions.push.engine.system.PubUtils;
import org.tiogasolutions.push.kernel.accounts.DomainStore;
import org.tiogasolutions.push.kernel.clients.DomainProfileEntity;
import org.tiogasolutions.push.kernel.execution.ExecutionManager;
import org.tiogasolutions.push.kernel.system.PluginManager;
import org.tiogasolutions.push.pub.domain.PubDomain;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class DomainResourceV3 {

    private final PubUtils pubUtils;
    private final String domainName;
    private final ExecutionManager executionManager;

    public DomainResourceV3(ExecutionManager executionManager, PubUtils pubUtils, String domainName) {
        this.executionManager = executionManager;
        this.domainName = domainName;
        this.pubUtils = pubUtils;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDomain() {

        DomainStore domainStore = executionManager.getDomainStore();
        DomainProfileEntity domainProfile = domainStore.getByDomainKey(domainName);

        if (domainProfile == null) {
          throw ApiException.notFound("The specified domain was not found.");
        }

        this.executionManager.getContext().setDomain(domainProfile);

        PluginManager pluginManager = executionManager.getPluginManager();
        PubDomain domain = pubUtils.toPushDomain(HttpStatusCode.OK, domainProfile, pluginManager);
        return pubUtils.toResponse(domain).build();
    }
}
