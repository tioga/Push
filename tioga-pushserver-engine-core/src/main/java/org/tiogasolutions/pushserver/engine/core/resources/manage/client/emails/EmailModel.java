/*
 * Copyright (c) 2014 Jacob D. Parr
 *
 * This software may not be used without permission.
 */
package org.tiogasolutions.pushserver.engine.core.resources.manage.client.emails;

import org.tiogasolutions.pushserver.common.accounts.Account;
import org.tiogasolutions.pushserver.common.clients.Domain;
import org.tiogasolutions.pushserver.common.requests.PushRequest;
import org.tiogasolutions.pushserver.pub.push.EmailPush;

public class EmailModel {

  private final Account account;
  private final Domain domain;

  private final PushRequest request;
  private final EmailPush email;

  public EmailModel(Account account, Domain domain, PushRequest request, EmailPush email) {
    this.account = account;
    this.domain = domain;
    this.request = request;
    this.email = email;
  }

  public Account getAccount() {
    return account;
  }

  public Domain getDomain() {
    return domain;
  }

  public PushRequest getRequest() {
    return request;
  }

  public EmailPush getEmail() {
    return email;
  }

  public String getRetryUrl() {
    return String.format("/manage/domain/%s/emails/%s/retry", domain.getDomainKey(), request.getPushRequestId());
  }
}
