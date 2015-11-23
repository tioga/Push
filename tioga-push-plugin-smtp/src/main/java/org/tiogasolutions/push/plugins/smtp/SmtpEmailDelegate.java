/*
 * Copyright (c) 2014 Jacob D. Parr
 *
 * This software may not be used without permission.
 */

package org.tiogasolutions.push.plugins.smtp;

import org.tiogasolutions.apis.bitly.BitlyApis;
import org.tiogasolutions.dev.common.StringUtils;
import org.tiogasolutions.dev.common.exceptions.ExceptionUtils;
import org.tiogasolutions.dev.domain.comm.AuthenticationMethod;
import org.tiogasolutions.push.jackson.PushObjectMapper;
import org.tiogasolutions.push.kernel.AbstractDelegate;
import org.tiogasolutions.push.kernel.execution.ExecutionContext;
import org.tiogasolutions.push.kernel.requests.PushRequest;
import org.tiogasolutions.push.kernel.requests.PushRequestStore;
import org.tiogasolutions.push.pub.SmtpEmailPush;
import org.tiogasolutions.push.pub.common.RequestStatus;

public class SmtpEmailDelegate extends AbstractDelegate {

  private final SmtpEmailPush push;
  private final SmtpEmailConfig config;
  private final BitlyApis bitlyApis;

  public SmtpEmailDelegate(ExecutionContext executionContext, PushObjectMapper objectMapper, PushRequestStore pushRequestStore, BitlyApis bitlyApis, PushRequest pushRequest, SmtpEmailPush push, SmtpEmailConfig config) {
    super(executionContext, objectMapper, pushRequestStore, pushRequest);
    this.push = ExceptionUtils.assertNotNull(push, "push");
    this.config = ExceptionUtils.assertNotNull(config, "config");
    this.bitlyApis = bitlyApis;
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

    EmailMessage message;

    if (StringUtils.isNotBlank(config.getRecipientOverride())) {
      // This is NOT a "production" request.
      message = new EmailMessage(config.getServerName(), config.getPortNumber(), config.getRecipientOverride());
      apiMessage = String.format("Request sent to recipient override, %s.", config.getRecipientOverride());
    } else {
      // This IS a "production" request.
      message = new EmailMessage(config.getServerName(), config.getPortNumber(), push.getToAddress());
    }

    if (config.getAuthType().isTls()) {
      message.setAuthentication(AuthenticationMethod.TLS, config.getUserName(), config.getPassword());
    } else if (config.getAuthType().isSsl()) {
      message.setAuthentication(AuthenticationMethod.SSL, config.getUserName(), config.getPassword());
    } else {
      message.setAuthentication(AuthenticationMethod.NONE, config.getUserName(), config.getPassword());
    }

    message.setFrom(push.getFromAddress());

    String subject = push.getEmailSubject();
    subject = bitlyApis.parseAndShorten(subject);

    message.send(subject, null, push.getHtmlContent());

    return apiMessage;
  }
}
