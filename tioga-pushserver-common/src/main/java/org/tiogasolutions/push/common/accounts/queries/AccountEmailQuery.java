/*
 * Copyright (c) 2014 Jacob D. Parr
 *
 * This software may not be used without permission.
 */

package org.tiogasolutions.push.common.accounts.queries;

import org.tiogasolutions.push.pub.internal.RequestErrors;
import org.tiogasolutions.push.pub.internal.ValidationUtils;

public class AccountEmailQuery extends AccountQuery {

  private final String emailAddress;

  protected AccountEmailQuery(String emailAddress) {
    this.emailAddress = emailAddress;
  }

  public String getEmailAddress() {
    return emailAddress;
  }

  @Override
  public RequestErrors validate(RequestErrors errors) {
    ValidationUtils.requireValue(errors, emailAddress, "The email address must be specified.");
    return errors;
  }
}
