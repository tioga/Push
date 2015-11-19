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
import org.tiogasolutions.dev.common.IoUtils;
import org.tiogasolutions.push.kernel.KernelUtils;
import org.tiogasolutions.push.kernel.clients.DomainProfileEntity;
import org.tiogasolutions.push.kernel.execution.ExecutionContext;
import org.tiogasolutions.push.kernel.execution.ExecutionManager;
import org.tiogasolutions.push.kernel.plugins.PluginSupport;
import org.tiogasolutions.push.kernel.requests.PushRequest;
import org.tiogasolutions.push.pub.TwilioSmsPush;
import org.tiogasolutions.push.pub.common.Push;

import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.tiogasolutions.dev.common.StringUtils.nullToString;

public class TwilioPlugin extends PluginSupport {

  private TwilioConfigStore _configStore;

  public TwilioPlugin() {
      super(TwilioSmsPush.PUSH_TYPE);
  }

  public TwilioConfigStore getConfigStore(ExecutionManager executionManager) {
      if (_configStore == null) {
          _configStore = new TwilioConfigStore(executionManager);
      }
      return _configStore;
  }

  @Override
  public TwilioConfig getConfig(DomainProfileEntity domainProfile) {
    String docId = TwilioConfigStore.toDocumentId(domainProfile);
    return getConfigStore(executionManager).getByDocumentId(docId);
  }

  @Override
  public TwilioDelegate newDelegate(DomainProfileEntity domainProfile, PushRequest pushRequest, Push push) {
    TwilioConfig config = getConfig(domainProfile);
    return new TwilioDelegate(executionManager.context(), pushRequest, (TwilioSmsPush)push, config);
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

    String ACCOUNT_SID = config.getAccountSid();
    String AUTH_TOKEN = config.getAuthToken();

    TwilioRestClient client = new TwilioRestClient(ACCOUNT_SID, AUTH_TOKEN);

    // Build a filter for the MessageList
    List<NameValuePair> params = new ArrayList<NameValuePair>();
    params.add(new BasicNameValuePair("Body", "Twilio test message from Cosmic Push"));
    params.add(new BasicNameValuePair("To", config.getRecipient()));
    params.add(new BasicNameValuePair("From", config.getFromPhoneNumber()));
    MessageFactory messageFactory = client.getAccount().getMessageFactory();
    messageFactory.create(params);
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
