/*
 * Copyright (c) 2014 Jacob D. Parr
 *
 * This software may not be used without permission.
 */

package org.tiogasolutions.push.plugins.xmpp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.tiogasolutions.apis.bitly.BitlyApis;
import org.tiogasolutions.dev.common.StringUtils;
import org.tiogasolutions.dev.common.exceptions.ExceptionUtils;
import org.tiogasolutions.push.jackson.CpObjectMapper;
import org.tiogasolutions.push.kernel.AbstractDelegate;
import org.tiogasolutions.push.kernel.execution.ExecutionContext;
import org.tiogasolutions.push.kernel.requests.PushRequest;
import org.tiogasolutions.push.kernel.requests.PushRequestStore;
import org.tiogasolutions.push.pub.XmppPush;
import org.tiogasolutions.push.pub.common.RequestStatus;

public class XmppDelegate extends AbstractDelegate {

  private static final Log log = LogFactory.getLog(XmppDelegate.class);

  private final XmppPush push;
  private final XmppConfig config;
  private final BitlyApis bitlyApis;

  public XmppDelegate(ExecutionContext executionContext, CpObjectMapper objectMapper, PushRequestStore pushRequestStore, BitlyApis bitlyApis, PushRequest pushRequest, XmppPush push, XmppConfig config) {
    super(executionContext, objectMapper, pushRequestStore, pushRequest);
    this.config = ExceptionUtils.assertNotNull(config, "config");
    this.push = ExceptionUtils.assertNotNull(push, "push");
    this.bitlyApis = bitlyApis;
  }

  @Override
  public synchronized RequestStatus processRequest() throws Exception {

    String apiMessage = sendMessage();

    return pushRequest.processed(apiMessage);
  }

  public String sendMessage() throws Exception {

    String message = push.getMessage();
    message = bitlyApis.parseAndShorten(message);

    if (StringUtils.isNotBlank(config.getRecipientOverride())) {
      // This is NOT a "production" request.
      sendTo(config.getRecipientOverride(), message);
      return String.format("Request sent to recipient override, %s.", config.getRecipientOverride());

    } else {
      // This IS a "production" request.
      sendTo(push.getRecipient(), message);
      return null;
    }
  }

  public synchronized void sendTo(final String recipient, final String message) throws Exception {

    log.info(String.format("%s: %s", recipient, message));
    XMPPTCPConnection connection = null;

    try {
      XMPPTCPConnectionConfiguration.Builder builder = XMPPTCPConnectionConfiguration.builder();
      builder.setUsernameAndPassword(config.getUsername(), config.getPassword());
      builder.setHost(config.getHost());
      builder.setPort(config.getPortInt());
      builder.setServiceName(config.getServiceName());

      connection = new XMPPTCPConnection(builder.build());
      connection.setPacketReplyTimeout(30 * 1000);

      connection.connect();
      connection.login();
      connection.sendStanza(new Presence(Presence.Type.available));

      Message jabberMessage = new Message(recipient, Message.Type.chat);
      jabberMessage.setBody(message);

      connection.sendStanza(jabberMessage);

    } finally {
      disconnect(connection);
    }
  }

  private void disconnect(XMPPTCPConnection connection) {
    try {
      if (connection != null && connection.isConnected()) {
        connection.disconnect(new Presence(Presence.Type.unavailable));
      }
    } catch (SmackException.NotConnectedException ignored) {
      /* ignored */
    }
  }
}
