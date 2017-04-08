package org.tiogasolutions.push.kernel.config;

public class SystemConfiguration {

    private final String accessControlAllowOrigin;

    public SystemConfiguration(String accessControlAllowOrigin) {
        this.accessControlAllowOrigin = accessControlAllowOrigin;
    }

    public String getAccessControlAllowOrigin() {
        return accessControlAllowOrigin;
    }
}
