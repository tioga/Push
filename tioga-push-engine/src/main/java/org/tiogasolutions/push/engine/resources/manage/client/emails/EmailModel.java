/*
 * Copyright (c) 2014 Jacob D. Parr
 *
 * This software may not be used without permission.
 */
package org.tiogasolutions.push.engine.resources.manage.client.emails;

import org.tiogasolutions.push.kernel.accounts.Account;
import org.tiogasolutions.push.kernel.clients.DomainProfileEntity;
import org.tiogasolutions.push.kernel.requests.PushRequest;
import org.tiogasolutions.push.pub.common.CommonEmail;

public class EmailModel {

  private final Account account;
  private final DomainProfileEntity domain;

  private final PushRequest request;
  private final CommonEmail email;

  public EmailModel(Account account, DomainProfileEntity domain, PushRequest request, CommonEmail email) {
    this.account = account;
    this.domain = domain;
    this.request = request;
    this.email = email;
  }

  public Account getAccount() {
    return account;
  }

  public DomainProfileEntity getDomain() {
    return domain;
  }

  public PushRequest getRequest() {
    return request;
  }

  public CommonEmail getEmail() {
    return email;
  }

  public String getRetryUrl() {
    return String.format("/manage/domain/%s/emails/%s/retry", domain.getDomainKey(), request.getPushRequestId());
  }
}
