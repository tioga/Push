package org.tiogasolutions.push.pub.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PubXmppConfig {

    private final String username;
    private final String password;

    private final String host;
    private final String port;
    private final String serviceName;

    private final String testToAddress;
    private final String recipientOverride;

    @JsonCreator
    public PubXmppConfig(@JsonProperty("username") String username,
                         @JsonProperty("password") String password,
                         @JsonProperty("host") String host,
                         @JsonProperty("port") String port,
                         @JsonProperty("service") String serviceName,
                         @JsonProperty("testToAddress") String testToAddress,
                         @JsonProperty("recipientOverride") String recipientOverride) {

        this.username = username;
        this.password = password;
        this.host = host;
        this.port = port;
        this.serviceName = serviceName;
        this.testToAddress = testToAddress;
        this.recipientOverride = recipientOverride;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getHost() {
        return host;
    }

    public String getPort() {
        return port;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getTestToAddress() {
        return testToAddress;
    }

    public String getRecipientOverride() {
        return recipientOverride;
    }
}
