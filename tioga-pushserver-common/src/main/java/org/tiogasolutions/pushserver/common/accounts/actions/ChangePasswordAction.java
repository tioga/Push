/*
 * Copyright (c) 2014 Jacob D. Parr
 *
 * This software may not be used without permission.
 */

package org.tiogasolutions.pushserver.common.accounts.actions;

import org.tiogasolutions.dev.common.exceptions.ApiException;
import org.tiogasolutions.pushserver.pub.internal.RequestErrors;
import org.tiogasolutions.pushserver.pub.internal.ValidationUtils;

public class ChangePasswordAction extends AccountAction {

  private final String current;
  private final String password;
  private final String confirmed;

  public ChangePasswordAction(String current, String password, String confirmed) {
    this.current = current;
    this.password = password;
    this.confirmed = confirmed;
  }

  public String getCurrent() {
    return current;
  }

  public String getPassword() {
    return password;
  }

  public String getConfirmed() {
    return confirmed;
  }

  @Override
  public RequestErrors validate(RequestErrors errors) throws ApiException {
    ValidationUtils.requireValue(errors, current, "The current password must be specified.");
    ValidationUtils.requireValue(errors, password, "The new password must be specified.");
    ValidationUtils.requireValue(errors, confirmed, "The confirmed password must be specified.");
    return errors;
  }
}
