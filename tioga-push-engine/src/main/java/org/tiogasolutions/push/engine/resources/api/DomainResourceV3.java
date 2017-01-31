package org.tiogasolutions.push.engine.resources.api;

import org.tiogasolutions.push.engine.system.PubUtils;
import org.tiogasolutions.push.kernel.execution.ExecutionManager;

public class DomainResourceV3 {

    private final PubUtils pubUtils;
    private final String domainName;
    private final ExecutionManager executionManager;

    public DomainResourceV3(ExecutionManager executionManager, PubUtils pubUtils, String domainName) {
        this.executionManager = executionManager;
        this.domainName = domainName;
        this.pubUtils = pubUtils;
    }
}
