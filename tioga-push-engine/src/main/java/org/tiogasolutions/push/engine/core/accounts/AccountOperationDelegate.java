/*
 * Copyright (c) 2014 Jacob D. Parr
 *
 * This software may not be used without permission.
 */

package org.tiogasolutions.push.engine.core.accounts;

import org.tiogasolutions.push.kernel.accounts.Account;
import org.tiogasolutions.push.kernel.accounts.AccountStore;
import org.tiogasolutions.dev.common.IoUtils;
import org.tiogasolutions.dev.common.exceptions.ApiException;
import org.tiogasolutions.push.kernel.accounts.actions.*;

import java.net.URL;

public class AccountOperationDelegate {

  private final AccountStore store;

  public AccountOperationDelegate(AccountStore store) {
    this.store = store;
  }

  public Account executeOperation(Account account, ChangePasswordAction operation) {
    account.apply(operation);
    store.update(account);
    return account;
  }

  public Account executeOperation(Account account) {
    store.delete(account);
    return null;
  }

  public Account executeOperation(Account account, LogInAction operation) {

    if (account == null) {
      throw ApiException.badRequest(Account.INVALID_USER_NAME_OR_PASSWORD);
    }

    if (account.hasTempPassword()) {
      account.clearTempPassword();
      store.update(account);
    }

    return account;
  }

  public Account executeOperation(Account account, CreateAccountAction operation) {
    if (account != null) {
      String msg = String.format("The email address \"%s\" already exists.", operation.getEmailAddress());
      throw ApiException.badRequest(msg);
    }

    account = new Account(operation);
    store.create(account);

    return account;
  }

  public Account executeOperation(Account account, ResetPasswordAction operation) {
    if (account == null) {
      throw ApiException.badRequest("The email address was not found.");
    }

    String tempPassword = account.createTempPassword();
    store.update(account);

    String message = String.format("<h1 style='margin-top:0'>Password Reset</h1><p>Your temporary password for %s is %s.</p>", account.getEmailAddress(), tempPassword);
    sendEmail(operation.getTemplateUrl(), account.getEmailAddress(), "Password Reset", message);

    return account;
  }

  private void sendEmail(URL templateUrl, String emailAddress, String subject, String message) {
    try {
      String template = IoUtils.toString(templateUrl);
      String content = template.replace("${message-content}", message);
//      AwsUtils.sendEmail(subject, content, "Munchie Monster <support@munchiemonster.com>", new MunMonEmailAddress(emailAddress));

    } catch (Exception e) {
      String msg = String.format("Exception sending email to %s", emailAddress);
      throw ApiException.internalServerError(msg, e);
    }
  }

  public Account executeOperation(Account account, ConfirmEmailAction operation) {
    account.confirmEmail(operation);
    store.update(account);
    return account;
  }

  public Account executeOperation(Account account, UpdateAccountAction operation) {
    // version check
    account.apply(operation);
    store.update(account);
    return account;
  }
}
