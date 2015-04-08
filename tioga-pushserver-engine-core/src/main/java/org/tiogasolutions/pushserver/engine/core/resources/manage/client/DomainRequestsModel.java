/*
 * Copyright (c) 2014 Jacob D. Parr
 *
 * This software may not be used without permission.
 */

package org.tiogasolutions.pushserver.engine.core.resources.manage.client;

import org.tiogasolutions.pushserver.common.accounts.Account;
import org.tiogasolutions.pushserver.common.clients.Domain;
import org.tiogasolutions.pushserver.common.requests.PushRequest;
import java.util.List;

public class DomainRequestsModel {

  private final Account account;
  private final Domain domain;
  private final List<PushRequest> requests;

  public DomainRequestsModel(Account account, Domain domain, List<PushRequest> requests) {
    this.account = account;
    this.domain = domain;
    this.requests = requests;
  }

  public Account getAccount() {
    return account;
  }

  public Domain getDomain() {
    return domain;
  }

  public List<PushRequest> getRequests() {
    return requests;
  }
}
