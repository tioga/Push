/*
 * Copyright (c) 2014 Jacob D. Parr
 *
 * This software may not be used without permission.
 */

package org.tiogasolutions.push.plugins.twilio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tiogasolutions.apis.bitly.BitlyApis;
import org.tiogasolutions.dev.common.BeanUtils;
import org.tiogasolutions.dev.common.Formats;
import org.tiogasolutions.dev.common.IoUtils;
import org.tiogasolutions.push.jackson.PushObjectMapper;
import org.tiogasolutions.push.kernel.KernelUtils;
import org.tiogasolutions.push.kernel.clients.DomainProfileEntity;
import org.tiogasolutions.push.kernel.execution.ExecutionContext;
import org.tiogasolutions.push.kernel.execution.ExecutionManager;
import org.tiogasolutions.push.kernel.plugins.PluginSupport;
import org.tiogasolutions.push.kernel.requests.PushRequest;
import org.tiogasolutions.push.kernel.requests.PushRequestStore;
import org.tiogasolutions.push.pub.TwilioSmsPush;
import org.tiogasolutions.push.pub.common.Push;

import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import java.io.InputStream;

import static org.tiogasolutions.dev.common.StringUtils.nullToString;

@Component
public class TwilioPlugin extends PluginSupport {

  private final BitlyApis bitlyApis;

  @Autowired
  public TwilioPlugin(ExecutionManager executionManager, PushObjectMapper objectMapper, PushRequestStore pushRequestStore, BitlyApis bitlyApis) {
    super(TwilioSmsPush.PUSH_TYPE, executionManager, objectMapper, pushRequestStore);
    this.bitlyApis = bitlyApis;
  }

  public TwilioConfigStore getConfigStore(ExecutionManager executionManager) {
    return new TwilioConfigStore(executionManager);
  }

  @Override
  public TwilioConfig getConfig(DomainProfileEntity domainProfile) {
    String docId = TwilioConfigStore.toDocumentId(domainProfile);
    return getConfigStore(executionManager).getByDocumentId(docId);
  }

  @Override
  public TwilioDelegate newDelegate(DomainProfileEntity domainProfile, PushRequest pushRequest, Push push) {
    TwilioConfig config = getConfig(domainProfile);
    return new TwilioDelegate(executionManager.context(), objectMapper, pushRequestStore, bitlyApis, pushRequest, (TwilioSmsPush)push, config);
  }

  @Override
  public void deleteConfig(DomainProfileEntity domainProfile) {
    TwilioConfig config = getConfig(domainProfile);

    if (config != null) {
        getConfigStore(executionManager).delete(config);
        executionManager.context().setLastMessage("Twilio SMS configuration deleted.");
    } else {
        executionManager.context().setLastMessage("Twilio SMS configuration doesn't exist.");
    }
  }

  @Override
  public void updateConfig(DomainProfileEntity domainProfile, MultivaluedMap<String, String> formParams) {
    UpdateTwilioConfigAction action = new UpdateTwilioConfigAction(domainProfile, formParams);

    TwilioConfig twilioConfig = getConfig(domainProfile);
    if (twilioConfig == null) {
        twilioConfig = new TwilioConfig();
    }

    twilioConfig.apply(action);
    getConfigStore(executionManager).update(twilioConfig);

    executionManager.context().setLastMessage("Twilio configuration updated.");
  }

  @Override
  public void test(DomainProfileEntity domainProfile) throws Exception {

    TwilioConfig config = getConfig(domainProfile);

    if (config == null) {
        String msg = "The Twilio config has not been specified.";
        executionManager.context().setLastMessage(msg);
        return;
    }

    String when = Formats.defaultStamp(new java.util.Date());
    TwilioSmsPush push = TwilioSmsPush.newPush(
      config.getFromPhoneNumber(), config.getRecipient(),
      String.format("Twilio test message from Cosmic Push sent at %s.", when),
      null, BeanUtils.toMap("smtp-test:true"));

    PushRequest pushRequest = new PushRequest(Push.CURRENT_API_VERSION, domainProfile, push);
    pushRequestStore.create(pushRequest);

    if (new TwilioDelegate(executionManager.context(), objectMapper, pushRequestStore, bitlyApis, pushRequest, push, config).execute(false)) {
      String msg = String.format("Test message sent from %s to %s:\n%s", config.getFromPhoneNumber(), config.getRecipient(), push.getMessage());
      executionManager.context().setLastMessage(msg);
    };
  }

  @Override
  public String getAdminUi(DomainProfileEntity domainProfile) throws IOException {
    ExecutionContext context = executionManager.context();
    String contextRoot = KernelUtils.getContextRoot(context.getUriInfo());

    TwilioConfig config = getConfig(domainProfile);

    InputStream stream = getClass().getResourceAsStream("/org/tiogasolutions/push/plugins/twilio/admin.html");
    String content = IoUtils.toString(stream);

    content = content.replace("${legend-class}",        nullToString(config == null ? "no-config" : ""));
    content = content.replace("${push-type}",           nullToString(getPushType().getCode()));
    content = content.replace("${domain-key}",          nullToString(domainProfile.getDomainKey()));
    content = content.replace("${context-root}",        nullToString(contextRoot));

    content = content.replace("${config-account-sid}",  nullToString(config == null ? null : config.getAccountSid()));
    content = content.replace("${config-auth-token}",   nullToString(config == null ? null : config.getAuthToken()));
    content = content.replace("${config-from-number}",  nullToString(config == null ? null : config.getFromPhoneNumber()));
    content = content.replace("${config-recipient}",    nullToString(config == null ? null : config.getRecipient()));

    if (content.contains("${")) {
        String msg = "The Twilio admin UI still contains un-parsed elements.";
        throw new IllegalStateException(msg);
    }

    return content;
  }
}
