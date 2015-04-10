/*
 * Copyright (c) 2014 Jacob D. Parr
 *
 * This software may not be used without permission.
 */

package org.tiogasolutions.push.plugins.ses;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;
import com.amazonaws.services.simpleemail.model.*;
import org.tiogasolutions.push.common.AbstractDelegate;
import org.tiogasolutions.push.common.clients.Domain;
import org.tiogasolutions.push.common.plugins.PluginContext;
import org.tiogasolutions.push.common.requests.PushRequest;
import org.tiogasolutions.push.common.system.AppContext;
import org.tiogasolutions.push.pub.common.RequestStatus;
import org.tiogasolutions.push.pub.SesEmailPush;
import org.tiogasolutions.dev.common.StringUtils;
import org.tiogasolutions.dev.common.exceptions.ExceptionUtils;

public class SesEmailDelegate extends AbstractDelegate {

  private final Domain domain;

  private final SesEmailPush push;
  private final SesEmailConfig config;
  private final AppContext appContext;

  public SesEmailDelegate(PluginContext pluginContext, Domain domain, PushRequest pushRequest, SesEmailPush push, SesEmailConfig config) {
    super(pluginContext, pushRequest);
    this.push = ExceptionUtils.assertNotNull(push, "push");
    this.config = ExceptionUtils.assertNotNull(config, "config");
    this.domain = ExceptionUtils.assertNotNull(domain, "domain");
    this.appContext = pluginContext.getAppContext();
  }

  @Override
  public synchronized RequestStatus processRequest() throws Exception {

    String apiMessage = sendEmail();

    return pushRequest.processed(apiMessage);
  }

  /**
   * Backdoor to the SesEmailDelegate to send an email message without any validation.
   * @return The API message to log into history.
   */
  public String sendEmail() {
    String apiMessage = null;

    Body body = new Body();
    if (StringUtils.isBlank(push.getHtmlContent())) {
      body.withText(new Content().withCharset("UTF-8").withData("-no message-"));
    } else {
      body.withHtml(new Content().withCharset("UTF-8").withData(push.getHtmlContent()));
    }

    SendEmailRequest sendEmailRequest = new SendEmailRequest();
    sendEmailRequest.withSource(push.getFromAddress());
    sendEmailRequest.withReturnPath(push.getFromAddress());
    sendEmailRequest.withReplyToAddresses(push.getFromAddress());

    if (StringUtils.isNotBlank(config.getRecipientOverride())) {
      // This is NOT a "production" request.
      sendEmailRequest.setDestination(new Destination().withToAddresses(config.getRecipientOverride()));
      apiMessage = String.format("Request sent to recipient override, %s.", config.getRecipientOverride());
    } else {
      // This IS a "production" request.
      sendEmailRequest.setDestination(new Destination().withToAddresses(push.getToAddress()));
    }

    String subject = push.getEmailSubject();
    subject = appContext.getBitlyApi().parseAndShorten(subject);
    Content subjectContent = new Content().withCharset("UTF-8").withData(subject);

    sendEmailRequest.setMessage(new Message(subjectContent, body));

    AWSCredentials awsCredentials = new BasicAWSCredentials(config.getAccessKeyId(), config.getSecretKey());
    AmazonSimpleEmailServiceClient client = new AmazonSimpleEmailServiceClient(awsCredentials);
    client.setEndpoint(config.getEndpoint());
    client.sendEmail(sendEmailRequest);

    return apiMessage;
  }
}
