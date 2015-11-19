/*
 * Copyright (c) 2014 Jacob D. Parr
 *
 * This software may not be used without permission.
 */

package org.tiogasolutions.push.kernel.accounts;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tiogasolutions.couchace.core.api.CouchDatabase;
import org.tiogasolutions.lib.couchace.DefaultCouchStore;
import org.tiogasolutions.push.kernel.accounts.queries.AccountEmailQuery;
import org.tiogasolutions.push.kernel.accounts.queries.AccountEntityQuery;
import org.tiogasolutions.push.kernel.accounts.queries.AccountIdQuery;
import org.tiogasolutions.push.kernel.accounts.queries.AccountQuery;
import org.tiogasolutions.push.kernel.config.CouchServersConfig;
import org.tiogasolutions.push.kernel.system.PushCouchServer;
import org.tiogasolutions.push.pub.internal.RequestErrors;

import java.util.List;

@Component
public class AccountStore extends DefaultCouchStore<Account> {

  public static final String ACCOUNT_DESIGN_NAME = "account";

  private final String databaseName;

  @Autowired
  public AccountStore(PushCouchServer couchServer, CouchServersConfig config) {
    super(couchServer, Account.class);
    this.databaseName = config.getMasterDatabaseName();
  }

  @Override
  public String getDatabaseName() {
    return databaseName;
  }

  @Override
  public String getDesignName() {
    return ACCOUNT_DESIGN_NAME;
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

  @Override
  public void createDatabase(CouchDatabase database) {
    PushCouchServer.createMainDatabase(database);
  }
}
