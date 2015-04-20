package org.tiogasolutions.push.common.plugins;

import org.tiogasolutions.push.common.AbstractDelegate;
import org.tiogasolutions.push.common.clients.Domain;
import org.tiogasolutions.push.common.requests.PushRequest;
import org.tiogasolutions.push.common.system.DomainDatabaseConfig;
import org.tiogasolutions.push.common.system.PluginManager;
import org.tiogasolutions.push.pub.EmailPush;
import org.tiogasolutions.push.pub.SesEmailPush;
import org.tiogasolutions.push.pub.SmtpEmailPush;
import org.tiogasolutions.push.pub.common.PingPush;
import org.tiogasolutions.push.pub.common.Push;
import org.tiogasolutions.push.pub.common.PushResponse;
import org.tiogasolutions.push.pub.common.RequestStatus;

import java.time.LocalDateTime;
import java.util.Collections;

public class PushProcessor {

  private final PluginContext context;

  public PushProcessor(PluginContext context) {
    this.context = context;
  }

  public PushResponse execute(int apiVersion, Domain domain, Push push) {

    if (push instanceof PingPush) {
      return new PushResponse(
        domain.getDomainId(),
        "0",
        LocalDateTime.now(),
        RequestStatus.processed,
        Collections.emptyList()
      );
    }

    // TODO - validate the remoteHost and remoteAddress specified in the push as really coming from them.
    boolean converted = false;

    if (EmailPush.class.equals(push.getClass())) {
      // Email pushes need to be converted to SES or SMTP. We prefer
      // SES because it amounts to a rest call compared to SMTP for
      // which we have to open and close the SMTP connections.

      EmailPush emailPush = (EmailPush)push;
      DomainDatabaseConfig databaseConfig = context.getDatabaseConfig();

      if (PluginManager.getPlugin(SesEmailPush.PUSH_TYPE).getConfig(databaseConfig, domain) != null) {
        // We have an SES config, use it.
        converted = true;
        push = SesEmailPush.newPush(emailPush);

      } else if (PluginManager.getPlugin(SmtpEmailPush.PUSH_TYPE).getConfig(databaseConfig, domain) != null) {
        // We have an SMTP config, use it.
        converted = true;
        push = SmtpEmailPush.newPush(emailPush);

      } else {
        // We might fail where because we have
        // neither SES nor SMTP configurations.

        PushRequest pushRequest = new PushRequest(apiVersion, domain, push);
        pushRequest.failed("Neither a SES nor SMTP configuration was specified.");
        context.getPushRequestStore().create(pushRequest);

        return new PushResponse(
            domain.getDomainId(),
            pushRequest.getPushRequestId(),
            pushRequest.getCreatedAt(),
            pushRequest.getRequestStatus(),
            pushRequest.getNotes()
        );
      }
    }

    PushRequest pushRequest = new PushRequest(apiVersion, domain, push);
    if (converted) {
      pushRequest.addNote("Converted from an email-push.");
    }
    context.getPushRequestStore().create(pushRequest);

    Plugin plugin = PluginManager.getPlugin(push.getPushType());
    AbstractDelegate delegate = plugin.newDelegate(context, domain, pushRequest, push);

    delegate.start();

    return new PushResponse(
        domain.getDomainId(),
        pushRequest.getPushRequestId(),
        pushRequest.getCreatedAt(),
        pushRequest.getRequestStatus(),
        pushRequest.getNotes()
    );
  }
}
