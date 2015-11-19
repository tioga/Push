/*
 * Copyright (c) 2014 Jacob D. Parr
 *
 * This software may not be used without permission.
 */

package org.tiogasolutions.push.plugins.xmpp;

import org.jivesoftware.smack.XMPPException;
import org.tiogasolutions.apis.bitly.BitlyApis;
import org.tiogasolutions.dev.common.StringUtils;
import org.tiogasolutions.dev.common.exceptions.ExceptionUtils;
import org.tiogasolutions.push.kernel.AbstractDelegate;
import org.tiogasolutions.push.kernel.execution.ExecutionContext;
import org.tiogasolutions.push.kernel.requests.PushRequest;
import org.tiogasolutions.push.pub.XmppPush;
import org.tiogasolutions.push.pub.common.RequestStatus;

public class XmppDelegate extends AbstractDelegate {

  private final XmppPush push;
  private final XmppConfig config;

  public XmppDelegate(ExecutionContext executionContext, PushRequest pushRequest, XmppPush push, XmppConfig config) {
    super(executionContext, pushRequest);
    this.config = ExceptionUtils.assertNotNull(config, "config");
    this.push = ExceptionUtils.assertNotNull(push, "push");
  }

  @Override
  public synchronized RequestStatus processRequest() throws Exception {

    String apiMessage = sendMessage();

    return pushRequest.processed(apiMessage);
  }

  public String sendMessage() throws XMPPException {

    XmppFactory factory = new XmppFactory(config);

    String message = push.getMessage();
    BitlyApis bitlyApis = executionContext.getBean(BitlyApis.class);
    message = bitlyApis.parseAndShorten(message);

    if (StringUtils.isNotBlank(config.getRecipientOverride())) {
      // This is NOT a "production" request.
      factory.sendTo(config.getRecipientOverride(), message);
      return String.format("Request sent to recipient override, %s.", config.getRecipientOverride());

    } else {
      // This IS a "production" request.
      factory.sendTo(push.getRecipient(), message);
      return null;
    }
  }
}
