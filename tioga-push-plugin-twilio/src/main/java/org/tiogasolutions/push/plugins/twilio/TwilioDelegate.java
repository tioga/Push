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
import org.tiogasolutions.dev.common.exceptions.ExceptionUtils;
import org.tiogasolutions.push.kernel.AbstractDelegate;
import org.tiogasolutions.push.kernel.execution.ExecutionContext;
import org.tiogasolutions.push.kernel.requests.PushRequest;
import org.tiogasolutions.push.pub.TwilioSmsPush;
import org.tiogasolutions.push.pub.common.RequestStatus;

import java.util.ArrayList;
import java.util.List;

public class TwilioDelegate extends AbstractDelegate {

  private final TwilioSmsPush push;
  private final TwilioConfig config;

  public TwilioDelegate(ExecutionContext executionContext, PushRequest pushRequest, TwilioSmsPush push, TwilioConfig config) {
    super(executionContext, pushRequest);
    this.config = ExceptionUtils.assertNotNull(config, "config");
    this.push = ExceptionUtils.assertNotNull(push, "push");
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
