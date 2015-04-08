/*
 * Copyright (c) 2014 Jacob D. Parr
 *
 * This software may not be used without permission.
 */

package org.tiogasolutions.pushserver.plugins.twilio;

import org.tiogasolutions.pushserver.common.AbstractDelegate;
import org.tiogasolutions.pushserver.common.clients.Domain;
import org.tiogasolutions.pushserver.common.plugins.PluginContext;
import org.tiogasolutions.pushserver.common.requests.PushRequest;
import org.tiogasolutions.pushserver.pub.common.RequestStatus;
import org.tiogasolutions.pushserver.pub.push.TwilioSmsPush;
import com.twilio.sdk.TwilioRestClient;
import com.twilio.sdk.resource.factory.MessageFactory;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.tiogasolutions.dev.common.exceptions.ExceptionUtils;

import java.util.ArrayList;
import java.util.List;

public class TwilioDelegate extends AbstractDelegate {

  private final Domain domain;

  private final TwilioSmsPush push;
  private final TwilioConfig config;

  public TwilioDelegate(PluginContext pluginContext, Domain domain, PushRequest pushRequest, TwilioSmsPush push, TwilioConfig config) {
    super(pluginContext, pushRequest);
    this.config = ExceptionUtils.assertNotNull(config, "config");
    this.push = ExceptionUtils.assertNotNull(push, "push");
    this.domain = ExceptionUtils.assertNotNull(domain, "domain");
  }

  @Override
  public synchronized RequestStatus processRequest() throws Exception {

    TwilioRestClient client = new TwilioRestClient(config.getAccountSid(), config.getAuthToken());

    // Build a filter for the MessageList
    List<NameValuePair> params = new ArrayList<NameValuePair>();
    params.add(new BasicNameValuePair("Body", push.getMessage()));
    params.add(new BasicNameValuePair("From", push.getFrom()));
    params.add(new BasicNameValuePair("To", push.getRecipient()));
    MessageFactory messageFactory = client.getAccount().getMessageFactory();
    messageFactory.create(params);

    return pushRequest.processed();
  }
}
