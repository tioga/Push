/*
 * Copyright (c) 2014 Jacob D. Parr
 *
 * This software may not be used without permission.
 */

package org.tiogasolutions.push.engine.core.accounts;

import org.tiogasolutions.push.kernel.accounts.actions.AccountAction;
import org.tiogasolutions.push.kernel.accounts.queries.AccountQuery;
import org.tiogasolutions.dev.common.exceptions.ExceptionUtils;

public class AccountRequest {

  private final AccountQuery query;
  private final AccountAction operation;

  public AccountRequest(AccountQuery query, AccountAction operation) {
    this.query = ExceptionUtils.assertNotNull(query, "query");
    this.operation = ExceptionUtils.assertNotNull(operation, "operation");
  }

  public AccountQuery getQuery() {
    return query;
  }

  public AccountAction getOperation() {
    return operation;
  }
}
