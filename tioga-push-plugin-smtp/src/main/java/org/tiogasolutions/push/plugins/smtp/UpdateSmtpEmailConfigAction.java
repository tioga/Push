/*
 * Copyright (c) 2014 Jacob D. Parr
 *
 * This software may not be used without permission.
 */

package org.tiogasolutions.push.plugins.smtp;

import org.tiogasolutions.dev.common.StringUtils;
import org.tiogasolutions.push.kernel.clients.DomainProfileEntity;
import org.tiogasolutions.push.pub.domain.SmtpAuthType;
import org.tiogasolutions.push.pub.internal.RequestErrors;
import org.tiogasolutions.push.pub.internal.ValidatableAction;
import org.tiogasolutions.push.pub.internal.ValidationUtils;

import java.util.Map;

public class UpdateSmtpEmailConfigAction implements ValidatableAction {

  private final DomainProfileEntity domain;

  private final String userName;
  private final String password;

  private final SmtpAuthType authType;
  private final String serverName;
  private final String portNumber;

  private final String recipientOverride;
  private String testToAddress;
  private String testFromAddress;

  public UpdateSmtpEmailConfigAction(DomainProfileEntity domain, Map<String, String> params) {

    this.domain = domain;

    this.userName = params.get("userName");
    this.password = params.get("password");

    this.authType = (params.containsKey("authType") == false) ? null : SmtpAuthType.valueOf(params.get("authType"));
    this.serverName = params.get("serverName");

    String portNumber = params.get("portNumber");
    if (StringUtils.isBlank(portNumber) && authType != null) {
      this.portNumber = authType.getDefaultPort();
    } else {
      this.portNumber = portNumber;
    }

    this.testToAddress = params.get("testToAddress");
    this.testFromAddress = params.get("testFromAddress");
    this.recipientOverride = params.get("recipientOverride");
  }

  public UpdateSmtpEmailConfigAction(DomainProfileEntity domain, String userName, String password, SmtpAuthType authType, String serverName, String portNumber, String testToAddress, String testFromAddress, String recipientOverride) {

    this.domain = domain;

    this.userName = userName;
    this.password = password;

    this.authType = authType;
    this.serverName = serverName;

    if (StringUtils.isBlank(portNumber) && authType != null) {
      this.portNumber = authType.getDefaultPort();
    } else {
      this.portNumber = portNumber;
    }

    this.testToAddress = testToAddress;
    this.testFromAddress = testFromAddress;
    this.recipientOverride = recipientOverride;
  }

  @Override
  public RequestErrors validate(RequestErrors errors) {

    ValidationUtils.requireValue(errors, domain, "The domain must be specified.");

    ValidationUtils.requireValue(errors, userName, "The user's name must be specified.");
    ValidationUtils.requireValue(errors, password, "The password must be specified.");
    ValidationUtils.requireValue(errors, authType, "The authentication type must be specified.");
    ValidationUtils.requireValue(errors, serverName, "The server's name must be specified.");

    ValidationUtils.requireValue(errors, portNumber, "The server's port number must be specified.");
    ValidationUtils.requireInteger(errors, portNumber, "The server's port number is not a valid number.");

    return errors;
  }

  public DomainProfileEntity getDomain() {
    return domain;
  }

  public String getUserName() {
    return userName;
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

  public String getPortNumber() {
    return portNumber;
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
