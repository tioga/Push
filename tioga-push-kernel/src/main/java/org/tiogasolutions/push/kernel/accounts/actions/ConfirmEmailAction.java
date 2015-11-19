/*
 * Copyright (c) 2014 Jacob D. Parr
 *
 * This software may not be used without permission.
 */

package org.tiogasolutions.push.kernel.accounts.actions;

import org.tiogasolutions.dev.common.exceptions.ApiException;
import org.tiogasolutions.push.pub.internal.RequestErrors;
import org.tiogasolutions.push.pub.internal.ValidationUtils;

public class ConfirmEmailAction extends AccountAction {

  private final String confirmationCode;

  public ConfirmEmailAction(String confirmationCode) {
    this.confirmationCode = confirmationCode;
  }

  public String getConfirmationCode() {
    return confirmationCode;
  }

  @Override
  public RequestErrors validate(RequestErrors errors) throws ApiException {
    ValidationUtils.requireValue(errors, confirmationCode, "The confirmation code must be specified.");
    return errors;
  }
}
