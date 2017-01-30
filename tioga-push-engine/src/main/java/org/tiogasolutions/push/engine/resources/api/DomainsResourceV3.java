package org.tiogasolutions.push.engine.resources.api;

import org.tiogasolutions.push.engine.system.PubUtils;
import org.tiogasolutions.push.kernel.execution.ExecutionManager;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

public class DomainsResourceV3 {

    private final PubUtils pubUtils;
    private final ExecutionManager executionManager;

    public DomainsResourceV3(ExecutionManager executionManager, PubUtils pubUtils) {
        this.executionManager = executionManager;
        this.pubUtils = pubUtils;
    }

    @Path("{domainName}")
    public DomainResourceV3 getDomainResourceV3(@PathParam("domainName") String domainName) {
        return new DomainResourceV3(executionManager, pubUtils, domainName);
    }
}
