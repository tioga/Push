/*
 * Copyright (c) 2014 Jacob D. Parr
 *
 * This software may not be used without permission.
 */

package org.tiogasolutions.push.plugins.notifier;

import org.tiogasolutions.push.common.AbstractDelegate;
import org.tiogasolutions.push.common.clients.Domain;
import org.tiogasolutions.push.common.plugins.PluginContext;
import org.tiogasolutions.push.common.requests.PushRequest;
import org.tiogasolutions.push.common.system.AppContext;
import org.tiogasolutions.push.pub.common.RequestStatus;
import org.tiogasolutions.push.pub.XmppPush;
import org.tiogasolutions.push.pub.LqNotificationPush;
import org.tiogasolutions.dev.common.exceptions.ExceptionUtils;

public class LqNotificationsDelegate extends AbstractDelegate {

  private final Domain domain;

  private final LqNotificationPush push;
  private final LqNotificationsConfig config;
  private AppContext appContext;

  public LqNotificationsDelegate(PluginContext pluginContext, Domain domain, PushRequest pushRequest, LqNotificationPush push, LqNotificationsConfig config) {
    super(pluginContext, pushRequest);
    this.appContext = pluginContext.getAppContext();
    this.config = ExceptionUtils.assertNotNull(config, "config");
    this.push = ExceptionUtils.assertNotNull(push, "push");
    this.domain = ExceptionUtils.assertNotNull(domain, "domain");
  }

  @Override
  public synchronized RequestStatus processRequest() throws Exception {

    String id = pushRequest.getPushRequestId();
    String url = String.format("%s/q/%s", pluginContext.getBaseURI(), id);

    String message = push.getSummary() + " " + url;
    message = appContext.getBitlyApi().parseAndShorten(message);

    String recipient = config.getUserName();
    XmppPush push = XmppPush.newPush(recipient, message, null);

    pluginContext.getPushProcessor().execute(pushRequest.getApiVersion(), domain, push);
    return pushRequest.processed();
  }
}
