/*
 * Copyright (c) 2014 Jacob D. Parr
 *
 * This software may not be used without permission.
 */

package org.tiogasolutions.push.plugins.ses;

import org.tiogasolutions.push.kernel.clients.DomainProfileEntity;
import org.tiogasolutions.push.pub.internal.RequestErrors;
import org.tiogasolutions.push.pub.internal.ValidatableAction;
import org.tiogasolutions.push.pub.internal.ValidationUtils;

import java.util.Map;

public class UpdateSesEmailConfigAction implements ValidatableAction {

  private final DomainProfileEntity domain;

  private String accessKeyId;
  private String secretKey;
  private String endpoint;
  private String recipientOverride;
  private String testToAddress;
  private String testFromAddress;

  public UpdateSesEmailConfigAction(DomainProfileEntity domain, Map<String, String> params) {

    this.domain = domain;

    this.accessKeyId = params.get("accessKeyId");
    this.secretKey = params.get("secretKey");
    this.endpoint = params.get("endpoint");

    this.testToAddress = params.get("testToAddress");
    this.testFromAddress = params.get("testFromAddress");
    this.recipientOverride = params.get("recipientOverride");
  }

  public UpdateSesEmailConfigAction(DomainProfileEntity domain, String accessKeyId, String secretKey, String endpoint, String testToAddress, String testFromAddress, String recipientOverride) {

    this.domain = domain;

    this.accessKeyId = accessKeyId;
    this.secretKey = secretKey;
    this.endpoint = endpoint;

    this.testToAddress = testToAddress;
    this.testFromAddress = testFromAddress;
    this.recipientOverride = recipientOverride;
  }

  @Override
  public RequestErrors validate(RequestErrors errors) {

    ValidationUtils.requireValue(errors, accessKeyId, "The AWS-SES Access Key ID must be specified.");
    ValidationUtils.requireValue(errors, secretKey, "The AWS-SES Secret Key must be specified.");
    ValidationUtils.requireValue(errors, endpoint, "The AWS-SES Endpoint must be specified.");

    return errors;
  }

  public DomainProfileEntity getDomain() {
    return domain;
  }

  public String getAccessKeyId() {
    return accessKeyId;
  }

  public String getSecretKey() {
    return secretKey;
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

  public String getEndpoint() {
    return endpoint;
  }
}
