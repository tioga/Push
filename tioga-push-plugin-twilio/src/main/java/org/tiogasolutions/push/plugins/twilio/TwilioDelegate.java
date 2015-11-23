/*
 * Copyright (c) 2014 Jacob D. Parr
 *
 * This software may not be used without permission.
 */

package org.tiogasolutions.push.plugins.twilio;

import com.twilio.sdk.TwilioRestClient;
import com.twilio.sdk.resource.factory.MessageFactory;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.tiogasolutions.apis.bitly.BitlyApis;
import org.tiogasolutions.dev.common.exceptions.ExceptionUtils;
import org.tiogasolutions.push.jackson.PushObjectMapper;
import org.tiogasolutions.push.kernel.AbstractDelegate;
import org.tiogasolutions.push.kernel.execution.ExecutionContext;
import org.tiogasolutions.push.kernel.requests.PushRequest;
import org.tiogasolutions.push.kernel.requests.PushRequestStore;
import org.tiogasolutions.push.pub.TwilioSmsPush;
import org.tiogasolutions.push.pub.common.RequestStatus;

import java.util.ArrayList;
import java.util.List;

public class TwilioDelegate extends AbstractDelegate {

  private final TwilioSmsPush push;
  private final TwilioConfig config;
  private final BitlyApis bitlyApis;

  public TwilioDelegate(ExecutionContext executionContext, PushObjectMapper objectMapper, PushRequestStore pushRequestStore, BitlyApis bitlyApis, PushRequest pushRequest, TwilioSmsPush push, TwilioConfig config) {
    super(executionContext, objectMapper, pushRequestStore, pushRequest);
    this.config = ExceptionUtils.assertNotNull(config, "config");
    this.push = ExceptionUtils.assertNotNull(push, "push");
    this.bitlyApis = bitlyApis;
  }

  @Override
  public synchronized RequestStatus processRequest() throws Exception {

    TwilioRestClient client = new TwilioRestClient(config.getAccountSid(), config.getAuthToken());

    String message = bitlyApis.parseAndShorten(push.getMessage());

    // Build a filter for the MessageList
    List<NameValuePair> params = new ArrayList<NameValuePair>();
    params.add(new BasicNameValuePair("Body", message));
    params.add(new BasicNameValuePair("From", push.getFrom()));
    params.add(new BasicNameValuePair("To", push.getRecipient()));
    MessageFactory messageFactory = client.getAccount().getMessageFactory();
    messageFactory.create(params);

    return pushRequest.processed();
  }
}
