/*
 * Copyright (c) 2014 Jacob D. Parr
 *
 * This software may not be used without permission.
 */

package org.tiogasolutions.pushserver.engine.core.resources.api.deprecated;

import org.tiogasolutions.pushserver.engine.core.deprecated.NotificationPushV1;
import org.tiogasolutions.pushserver.common.AbstractDelegate;
import org.tiogasolutions.pushserver.common.clients.Domain;
import org.tiogasolutions.pushserver.common.plugins.PluginContext;
import org.tiogasolutions.pushserver.common.requests.PushRequest;
import org.tiogasolutions.pushserver.common.system.AppContext;
import org.tiogasolutions.pushserver.pub.common.RequestStatus;
import org.tiogasolutions.pushserver.pub.push.XmppPush;
import org.tiogasolutions.dev.common.exceptions.ExceptionUtils;

public class NotificationDelegateV1 extends AbstractDelegate {

  private final Domain domain;

  private final PluginContext pluginContext;
  private final NotificationPushV1 push;

  private final AppContext appContext;

  public NotificationDelegateV1(PluginContext pluginContext, Domain domain, PushRequest pushRequest, NotificationPushV1 push) {
    super(pluginContext, pushRequest);
    this.push = ExceptionUtils.assertNotNull(push, "push");
    this.pluginContext = ExceptionUtils.assertNotNull(pluginContext, "context");
    this.domain = ExceptionUtils.assertNotNull(domain, "domain");
    this.appContext = pluginContext.getAppContext();
  }

  @Override
  public synchronized RequestStatus processRequest() throws Exception {

    String message = push.getMessage();
    message = appContext.getBitlyApi().parseAndShorten(message);

    XmppPush push = XmppPush.newPush("jacob.parr@gmail.com", message, null);

    pluginContext.getPushProcessor().execute(pushRequest.getApiVersion(), domain, push);
    return pushRequest.processed();
  }
}
