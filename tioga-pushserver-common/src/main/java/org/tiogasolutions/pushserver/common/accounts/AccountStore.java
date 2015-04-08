/*
 * Copyright (c) 2014 Jacob D. Parr
 *
 * This software may not be used without permission.
 */

package org.tiogasolutions.pushserver.common.accounts;

import org.tiogasolutions.pushserver.common.accounts.queries.AccountEntityQuery;
import org.tiogasolutions.pushserver.common.accounts.queries.AccountQuery;
import org.tiogasolutions.pushserver.common.system.CpCouchServer;
import org.tiogasolutions.pushserver.pub.internal.RequestErrors;
import org.tiogasolutions.lib.couchace.DefaultCouchStore;
import org.tiogasolutions.pushserver.common.accounts.queries.AccountEmailQuery;
import org.tiogasolutions.pushserver.common.accounts.queries.AccountIdQuery;

import java.util.List;

public class AccountStore extends DefaultCouchStore<Account> {

  public static final String ACCOUNT_DESIGN_NAME = "account";

  public AccountStore(CpCouchServer couchServer) {
    super(couchServer, Account.class);
  }

  @Override
  public String getDesignName() {
    return ACCOUNT_DESIGN_NAME;
  }

  @Override
  public String getDatabaseName() {
    return CpCouchServer.DATABASE_NAME;
  }

  public Account get(AccountQuery accountQuery) {
    RequestErrors errors = new RequestErrors();
    accountQuery.validate(errors);
    if (errors.isNotEmpty()) {
      throw errors.toBadRequestException();
    }

    if (accountQuery instanceof AccountIdQuery) {
      AccountIdQuery query = (AccountIdQuery)accountQuery;
      return getByAccountId(query.getAccountId());

    } else if (accountQuery instanceof AccountEntityQuery) {
      AccountEntityQuery query = (AccountEntityQuery)accountQuery;
      return query.getAccount();

    } else if (accountQuery instanceof AccountEmailQuery) {
      AccountEmailQuery query = (AccountEmailQuery)accountQuery;
      return getByEmail(query.getEmailAddress());
    }

    String msg = String.format("The query %s is not supported.", accountQuery.getClass().getName());
    throw new UnsupportedOperationException(msg);
  }

  public Account getByAccountId(String accountId) {
    return super.getByDocumentId(accountId);
  }

  public List<Account> getAll() {
    return super.getEntities("byEmailAddress");
  }

  public Account getByEmail(String emailAddress) {
    if (emailAddress == null) return null;
    List<Account> response = super.getEntities("byEmailAddress", emailAddress);
    return response.isEmpty() ? null : response.get(0);
  }
}
