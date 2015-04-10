/*
 * Copyright (c) 2014 Jacob D. Parr
 *
 * This software may not be used without permission.
 */

package org.tiogasolutions.push.plugins.smtp;

import org.tiogasolutions.push.common.AbstractDelegate;
import org.tiogasolutions.push.common.clients.Domain;
import org.tiogasolutions.push.common.plugins.PluginContext;
import org.tiogasolutions.push.common.requests.PushRequest;
import org.tiogasolutions.push.common.system.AppContext;
import org.tiogasolutions.push.pub.common.RequestStatus;
import org.tiogasolutions.push.pub.SmtpEmailPush;
import org.tiogasolutions.dev.common.StringUtils;
import org.tiogasolutions.dev.common.exceptions.ExceptionUtils;
import org.tiogasolutions.dev.domain.comm.AuthenticationMethod;

public class SmtpEmailDelegate extends AbstractDelegate {

  private final Domain domain;

  private final SmtpEmailPush push;
  private final SmtpEmailConfig config;
  private final AppContext appContext;

  public SmtpEmailDelegate(PluginContext pluginContext, Domain domain, PushRequest pushRequest, SmtpEmailPush push, SmtpEmailConfig config) {
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
    subject = appContext.getBitlyApi().parseAndShorten(subject);

    message.send(subject, null, push.getHtmlContent());

    return apiMessage;
  }
}
