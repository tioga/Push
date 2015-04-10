/*
 * Copyright (c) 2014 Jacob D. Parr
 *
 * This software may not be used without permission.
 */

package org.tiogasolutions.push.engine.core.resources.manage.client.notifications;

import org.tiogasolutions.push.common.accounts.Account;
import org.tiogasolutions.push.common.clients.Domain;
import org.tiogasolutions.push.common.requests.PushRequest;
import org.tiogasolutions.push.pub.LqNotificationPush;

public class DomainNotificationModel {

  private final Account account;
  private final Domain domain;

  private final PushRequest request;
  private final LqNotificationPush notification;

  public DomainNotificationModel(Account account, Domain domain, PushRequest request, LqNotificationPush notification) {
    this.account = account;
    this.domain = domain;
    this.request = request;
    this.notification = notification;
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

  public LqNotificationPush getNotification() {
    return notification;
  }
}
