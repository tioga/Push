/*
 * Copyright (c) 2014 Jacob D. Parr
 *
 * This software may not be used without permission.
 */

package org.tiogasolutions.push.plugins.notifier;

import org.tiogasolutions.push.common.clients.Domain;
import org.tiogasolutions.push.pub.internal.RequestErrors;
import org.tiogasolutions.push.pub.internal.ValidatableAction;
import org.tiogasolutions.push.pub.internal.ValidationUtils;

import javax.ws.rs.core.MultivaluedMap;

public class UpdateLqNotificationsConfigAction implements ValidatableAction {

  private final Domain domain;
  private final String userName;

  public UpdateLqNotificationsConfigAction(Domain domain, MultivaluedMap<String, String> formParams) {
    this.domain = domain;
    this.userName = formParams.getFirst("userName");
  }

  public UpdateLqNotificationsConfigAction(Domain domain, String userName) {
    this.domain = domain;
    this.userName = userName;
  }

  @Override
  public RequestErrors validate(RequestErrors errors) {
    ValidationUtils.requireValue(errors, userName, "The user's name must be specified.");
    return errors;
  }

  public Domain getDomain() {
    return domain;
  }

  public String getUserName() {
    return userName;
  }
}
