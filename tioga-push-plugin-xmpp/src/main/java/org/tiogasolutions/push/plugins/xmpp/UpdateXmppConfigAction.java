/*
 * Copyright (c) 2014 Jacob D. Parr
 *
 * This software may not be used without permission.
 */

package org.tiogasolutions.push.plugins.xmpp;

import org.tiogasolutions.push.kernel.clients.DomainProfileEntity;
import org.tiogasolutions.push.pub.internal.RequestErrors;
import org.tiogasolutions.push.pub.internal.ValidatableAction;
import org.tiogasolutions.push.pub.internal.ValidationUtils;

import java.util.Map;

public class UpdateXmppConfigAction implements ValidatableAction {

  private final DomainProfileEntity domain;

  private final String username;
  private final String password;
  private final String recipientOverride;
  private String testAddress;
  private String host;
  private String port;
  private String serviceName;

  public UpdateXmppConfigAction(DomainProfileEntity domain, Map<String, String> params) {

    this.domain = domain;

    this.username = params.get("username");
    this.password = params.get("password");

    this.host = params.get("host");
    this.port = params.get("port");
    this.serviceName = params.get("serviceName");

    this.testAddress = params.get("testToAddress");
    this.recipientOverride = params.get("recipientOverride");
  }

  public UpdateXmppConfigAction(DomainProfileEntity domain, String username, String password, String host, String port, String serviceName, String testAddress, String recipientOverride) {

    this.domain = domain;

    this.username = username;
    this.password = password;

    this.host = host;
    this.port = port;
    this.serviceName = serviceName;

    this.testAddress = testAddress;
    this.recipientOverride = recipientOverride;
  }

  @Override
  public RequestErrors validate(RequestErrors errors) {
    ValidationUtils.requireValue(errors, username, "The user's name must be specified.");
    ValidationUtils.requireValue(errors, password, "The password must be specified.");

    ValidationUtils.requireValue(errors, host, "The host name must be specified.");
    ValidationUtils.requireValue(errors, port, "The port must be specified.");

    return errors;
  }

  public DomainProfileEntity getDomain() {
    return domain;
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }

  public String getRecipientOverride() {
    return recipientOverride;
  }

  public String getTestAddress() {
    return testAddress;
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
}
