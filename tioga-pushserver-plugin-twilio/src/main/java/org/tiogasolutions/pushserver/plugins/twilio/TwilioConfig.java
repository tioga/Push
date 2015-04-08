/*
 * Copyright (c) 2014 Jacob D. Parr
 *
 * This software may not be used without permission.
 */

package org.tiogasolutions.pushserver.plugins.twilio;

import org.tiogasolutions.pushserver.common.plugins.PluginConfig;
import org.tiogasolutions.pushserver.pub.internal.RequestErrors;
import org.tiogasolutions.couchace.annotations.CouchEntity;
import org.tiogasolutions.couchace.annotations.CouchId;
import org.tiogasolutions.couchace.annotations.CouchRevision;
import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.tiogasolutions.dev.common.EqualsUtils;

import java.io.Serializable;

@CouchEntity(TwilioConfigStore.TWILIO_CONFIG_DESIGN_NAME)
public class TwilioConfig implements PluginConfig, Serializable {

  private String configId;
  private String revision;

  private String domainId;

  private String accountSid;
  private String authToken;
  private String fromPhoneNumber;
  private String recipient;

  public TwilioConfig() {}

  @JsonCreator
  public TwilioConfig(@JacksonInject("configId") String configId,
                      @JacksonInject("revision") String revision,
                      @JsonProperty("domainId") String domainId,
                      @JsonProperty("accountSid") String accountSid,
                      @JsonProperty("authToken") String authToken,
                      @JsonProperty("fromPhoneNumber") String fromPhoneNumber,
                      @JsonProperty("recipient") String recipient) {
    this.configId = configId;
    this.revision = revision;

    this.domainId = domainId;

    this.accountSid = accountSid;
    this.authToken = authToken;
    this.fromPhoneNumber = fromPhoneNumber;
    this.recipient = recipient;
  }

  public TwilioConfig apply(UpdateTwilioConfigAction configAction) {
    configAction.validate(new RequestErrors()).assertNoErrors();

    if (domainId != null && EqualsUtils.objectsNotEqual(domainId, configAction.getDomain().getDomainId())) {
      String msg = "The specified Update Config Action domain type and this class are not for the same domain.";
      throw new IllegalArgumentException(msg);
    }

    this.domainId = configAction.getDomain().getDomainId();
    this.configId = TwilioConfigStore.toDocumentId(configAction.getDomain());

    this.accountSid = configAction.getAccountSid();
    this.authToken = configAction.getAuthToken();
    this.fromPhoneNumber = configAction.getFromPhoneNumber();
    this.recipient = configAction.getRecipient();

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

  public String getAccountSid() {
    return accountSid;
  }

  public String getAuthToken() {
    return authToken;
  }

  public String getFromPhoneNumber() {
    return fromPhoneNumber;
  }

  public String getRecipient() {
    return recipient;
  }
}
