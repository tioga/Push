/*
 * Copyright (c) 2014 Jacob D. Parr
 *
 * This software may not be used without permission.
 */

package org.tiogasolutions.push.engine.core.resources.manage.client;

import org.tiogasolutions.push.kernel.accounts.Account;
import org.tiogasolutions.push.kernel.clients.DomainProfileEntity;
import org.tiogasolutions.push.kernel.requests.PushRequest;
import java.util.List;

public class DomainRequestsModel {

  private final Account account;
  private final DomainProfileEntity domain;
  private final List<PushRequest> requests;

  public DomainRequestsModel(Account account, DomainProfileEntity domain, List<PushRequest> requests) {
    this.account = account;
    this.domain = domain;
    this.requests = requests;
  }

  public Account getAccount() {
    return account;
  }

  public DomainProfileEntity getDomain() {
    return domain;
  }

  public List<PushRequest> getRequests() {
    return requests;
  }
}
