package org.tiogasolutions.push.pub.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PubSmtpSettings {

    private final String username;
    private final String password;

    private final SmtpAuthType authType;
    private final String port;
    private final String serverName;

    private final String testToAddress;
    private final String testFromAddress;
    private final String recipientOverride;

    @JsonCreator
    public PubSmtpSettings(@JsonProperty("username") String username,
                           @JsonProperty("password") String password,
                           @JsonProperty("authType") SmtpAuthType authType,
                           @JsonProperty("port") String port,
                           @JsonProperty("serverName") String serverName,
                           @JsonProperty("testToAddress") String testToAddress,
                           @JsonProperty("testFromAddress") String testFromAddress,
                           @JsonProperty("recipientOverride") String recipientOverride) {

        this.username = username;
        this.password = password;

        this.authType = authType;
        this.port = port;
        this.serverName = serverName;

        this.testToAddress = testToAddress;
        this.testFromAddress = testFromAddress;
        this.recipientOverride = recipientOverride;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public SmtpAuthType getAuthType() {
        return authType;
    }

    public String getPort() {
        return port;
    }

    public String getServerName() {
        return serverName;
    }

    public String getTestToAddress() {
        return testToAddress;
    }

    public String getTestFromAddress() {
        return testFromAddress;
    }

    public String getRecipientOverride() {
        return recipientOverride;
    }
}
