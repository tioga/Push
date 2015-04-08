/*
 * Copyright (c) 2014 Jacob D. Parr
 *
 * This software may not be used without permission.
 */

package org.tiogasolutions.pushserver.engine.core.accounts;

import org.tiogasolutions.pushserver.common.accounts.queries.AccountQuery;
import org.tiogasolutions.pushserver.common.accounts.Account;

public interface AccountService {
  Account execute(AccountQuery query);
  Account execute(AccountRequest request);
}
