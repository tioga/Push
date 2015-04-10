/*
 * Copyright (c) 2014 Jacob D. Parr
 *
 * This software may not be used without permission.
 */

package org.tiogasolutions.push.engine.core.accounts;

import org.tiogasolutions.push.common.accounts.Account;
import org.tiogasolutions.push.common.accounts.AccountStore;
import org.tiogasolutions.push.common.accounts.actions.*;
import org.tiogasolutions.push.common.accounts.queries.AccountQuery;
import org.tiogasolutions.push.pub.internal.RequestErrors;
import org.tiogasolutions.dev.common.StringUtils;
import org.tiogasolutions.dev.common.exceptions.*;

public class AccountServiceCouch implements AccountService {

  private final AccountStore accountStore;
  private final AccountOperationDelegate accountDelegate;

  public AccountServiceCouch(AccountStore accountStore, AccountOperationDelegate accountDelegate) {
    this.accountStore = accountStore;
    this.accountDelegate = accountDelegate;
  }

  @Override
  public Account execute(AccountRequest purchaseRequest) {
    ExceptionUtils.assertNotNull(purchaseRequest, "request");
    AccountAction accountOperation = purchaseRequest.getOperation();

    RequestErrors errors = new RequestErrors();
    accountOperation.validate(errors);
    if (errors.isEmpty() == false) {
      String msg = StringUtils.toDelineatedString("\n", errors);
      throw ApiException.badRequest(msg);
    }

    Account account = accountStore.get(purchaseRequest.getQuery());

    if (accountOperation instanceof ChangePasswordAction) {
      ChangePasswordAction operation = (ChangePasswordAction)accountOperation;
      return accountDelegate.executeOperation(account, operation);

    } else if (accountOperation instanceof ConfirmEmailAction) {
      ConfirmEmailAction operation = (ConfirmEmailAction)accountOperation;
      return accountDelegate.executeOperation(account, operation);

    } else if (accountOperation instanceof CreateAccountAction) {
      CreateAccountAction operation = (CreateAccountAction)accountOperation;
      return accountDelegate.executeOperation(account, operation);

    } else if (accountOperation instanceof DeleteAccountAction) {
      DeleteAccountAction operation = (DeleteAccountAction)accountOperation;
      return accountDelegate.executeOperation(account);

    } else if (accountOperation instanceof LogInAction) {
      LogInAction operation = (LogInAction)accountOperation;
      return accountDelegate.executeOperation(account, operation);

    } else if (accountOperation instanceof ResetPasswordAction) {
      ResetPasswordAction operation = (ResetPasswordAction)accountOperation;
      return accountDelegate.executeOperation(account, operation);

    } else if (accountOperation instanceof UpdateAccountAction) {
      UpdateAccountAction operation = (UpdateAccountAction)accountOperation;
      return accountDelegate.executeOperation(account, operation);
    }

    String msg = String.format("The operation %s is not supported.", accountOperation.getClass().getName());
    throw new UnsupportedOperationException(msg);
  }

  @Override
  public Account execute(AccountQuery query) {
    return accountStore.get(query);
  }
}
