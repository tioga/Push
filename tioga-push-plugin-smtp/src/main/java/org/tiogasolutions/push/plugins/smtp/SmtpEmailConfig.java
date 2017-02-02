/*
 * Copyright (c) 2014 Jacob D. Parr
 *
 * This software may not be used without permission.
 */

package org.tiogasolutions.push.plugins.smtp;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.tiogasolutions.couchace.annotations.CouchEntity;
import org.tiogasolutions.couchace.annotations.CouchId;
import org.tiogasolutions.couchace.annotations.CouchRevision;
import org.tiogasolutions.dev.common.EqualsUtils;
import org.tiogasolutions.push.kernel.plugins.PluginConfig;
import org.tiogasolutions.push.pub.domain.SmtpAuthType;
import org.tiogasolutions.push.pub.internal.RequestErrors;

import java.io.Serializable;

@CouchEntity(SmtpEmailConfigStore.SMTP_EMAIL_CONFIG_DESIGN_NAME)
public class SmtpEmailConfig implements PluginConfig, Serializable {

    private String configId;
    private String revision;

    private String domainId;

    private String username;
    private String password;

    private SmtpAuthType authType;
    private String port;
    private String serverName;

    private String testToAddress;
    private String testFromAddress;
    private String recipientOverride;

    public SmtpEmailConfig() {
    }

    @JsonCreator
    public SmtpEmailConfig(@JacksonInject("configId") String configId,
                           @JacksonInject("revision") String revision,
                           @JsonProperty("domainId") String domainId,
                           @JsonProperty("username") String username,
                           @JsonProperty("password") String password,
                           @JsonProperty("authType") SmtpAuthType authType,
                           @JsonProperty("serverName") String serverName,
                           @JsonProperty("port") String port,
                           @JsonProperty("recipientOverride") String recipientOverride,
                           @JsonProperty("testToAddress") String testToAddress,
                           @JsonProperty("testFromAddress") String testFromAddress) {

        this.configId = configId;
        this.revision = revision;

        this.domainId = domainId;

        this.username = username;
        this.password = password;

        this.authType = authType;
        this.serverName = serverName;
        this.port = port;

        this.recipientOverride = recipientOverride;
        this.testToAddress = testToAddress;
        this.testFromAddress = testFromAddress;
    }

    public SmtpEmailConfig apply(UpdateSmtpEmailConfigAction action) {
        action.validate(new RequestErrors()).assertNoErrors();

        if (domainId != null && EqualsUtils.objectsNotEqual(domainId, action.getDomain().getDomainId())) {
            String msg = "The specified action and this class are not for the same domain.";
            throw new IllegalArgumentException(msg);
        }

        this.domainId = action.getDomain().getDomainId();
        this.configId = SmtpEmailConfigStore.toDocumentId(action.getDomain());

        this.username = action.getUsername();
        this.password = action.getPassword();

        this.authType = action.getAuthType();
        this.serverName = action.getServerName();
        this.port = action.getPortNumber();

        this.testToAddress = action.getTestToAddress();
        this.testFromAddress = action.getTestFromAddress();
        this.recipientOverride = action.getRecipientOverride();

        return this;
    }

    @CouchId
    public String getConfigId() {
        return configId;
    }

    @CouchRevision
    public String getRevision() {
        return revision;
    }

    public String getDomainId() {
        return domainId;
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

    public String getServerName() {
        return serverName;
    }

    public String getPort() {
        return port;
    }

    public int getPortInt() {
        return (port == null) ? 0 : Integer.valueOf(port);
    }

    public String getRecipientOverride() {
        return recipientOverride;
    }

    public String getTestToAddress() {
        return testToAddress;
    }

    public String getTestFromAddress() {
        return testFromAddress;
    }
}
