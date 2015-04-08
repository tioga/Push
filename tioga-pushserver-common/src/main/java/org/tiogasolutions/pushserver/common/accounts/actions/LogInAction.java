/*
 * Copyright (c) 2014 Jacob D. Parr
 *
 * This software may not be used without permission.
 */

package org.tiogasolutions.pushserver.common.accounts.actions;

import org.tiogasolutions.pushserver.common.accounts.Account;
import org.tiogasolutions.dev.common.StringUtils;
import org.tiogasolutions.dev.common.exceptions.ApiException;
import org.tiogasolutions.pushserver.pub.internal.RequestErrors;
import org.tiogasolutions.pushserver.pub.internal.ValidationUtils;

public class LogInAction extends AccountAction {

  private final String password;

  public LogInAction(String password) {
    this.password = StringUtils.emptyToNull(password);
  }

  public String getPassword() {
    return password;
  }

  @Override
  public RequestErrors validate(RequestErrors errors) throws ApiException {
    ValidationUtils.requireValue(errors, password, Account.INVALID_USER_NAME_OR_PASSWORD);
    return errors;
  }
}
