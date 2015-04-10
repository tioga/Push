/*
 * Copyright (c) 2014 Jacob D. Parr
 *
 * This software may not be used without permission.
 */

package org.tiogasolutions.push.engine.core.accounts;

import org.tiogasolutions.push.common.accounts.queries.AccountQuery;
import org.tiogasolutions.push.common.accounts.Account;

public interface AccountService {
  Account execute(AccountQuery query);
  Account execute(AccountRequest request);
}
