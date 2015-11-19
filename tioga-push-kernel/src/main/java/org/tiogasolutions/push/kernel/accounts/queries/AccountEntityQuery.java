/*
 * Copyright (c) 2014 Jacob D. Parr
 *
 * This software may not be used without permission.
 */

package org.tiogasolutions.push.kernel.accounts.queries;

import org.tiogasolutions.push.kernel.accounts.Account;
import org.tiogasolutions.push.pub.internal.RequestErrors;
import org.tiogasolutions.push.pub.internal.ValidationUtils;

public class AccountEntityQuery extends AccountQuery {

  private final Account account;

  protected AccountEntityQuery(Account account) {
    this.account = account;
  }

  public Account getAccount() {
    return account;
  }

  @Override
  public RequestErrors validate(RequestErrors errors) {
    ValidationUtils.requireValue(errors, account, "The account must be specified.");
    return errors;
  }
}
