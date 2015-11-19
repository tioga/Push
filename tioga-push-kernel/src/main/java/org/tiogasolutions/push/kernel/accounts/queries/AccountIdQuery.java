/*
 * Copyright (c) 2014 Jacob D. Parr
 *
 * This software may not be used without permission.
 */

package org.tiogasolutions.push.kernel.accounts.queries;

import org.tiogasolutions.push.pub.internal.RequestErrors;
import org.tiogasolutions.push.pub.internal.ValidationUtils;

public class AccountIdQuery extends AccountQuery {

  private final String accountId;

  protected AccountIdQuery(String accountId) {
    this.accountId = accountId;
  }

  public String getAccountId() {
    return accountId;
  }

  @Override
  public RequestErrors validate(RequestErrors errors) {
    ValidationUtils.requireValue(errors, accountId, "The account must be specified.");
    return errors;
  }
}
