/*
 * Copyright (c) 2014 Jacob D. Parr
 *
 * This software may not be used without permission.
 */

package org.tiogasolutions.push.engine.accounts;

import org.tiogasolutions.push.kernel.accounts.queries.AccountQuery;
import org.tiogasolutions.push.kernel.accounts.Account;

public interface AccountService {
  Account execute(AccountQuery query);
  Account execute(AccountRequest request);
}
