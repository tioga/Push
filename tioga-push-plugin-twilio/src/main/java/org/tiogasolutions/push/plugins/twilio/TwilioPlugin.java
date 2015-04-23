/*
 * Copyright (c) 2014 Jacob D. Parr
 *
 * This software may not be used without permission.
 */

package org.tiogasolutions.push.plugins.twilio;

import org.tiogasolutions.push.common.PushEnvUtils;
import org.tiogasolutions.push.common.clients.Domain;
import org.tiogasolutions.push.common.plugins.PluginContext;
import org.tiogasolutions.push.common.plugins.PluginSupport;
import org.tiogasolutions.push.common.requests.PushRequest;
import org.tiogasolutions.push.common.system.CpCouchServer;
import org.tiogasolutions.push.common.system.DomainDatabaseConfig;
import org.tiogasolutions.push.pub.common.Push;
import org.tiogasolutions.push.pub.TwilioSmsPush;
import com.twilio.sdk.TwilioRestClient;
import com.twilio.sdk.resource.factory.MessageFactory;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.tiogasolutions.dev.common.IoUtils;

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

    public TwilioConfigStore getConfigStore(DomainDatabaseConfig databaseConfig) {
        if (_configStore == null) {
            _configStore = new TwilioConfigStore(databaseConfig);
        }
        return _configStore;
    }

    @Override
    public TwilioConfig getConfig(DomainDatabaseConfig databaseConfig, Domain apiClient) {
        String docId = TwilioConfigStore.toDocumentId(apiClient);
        return getConfigStore(databaseConfig).getByDocumentId(docId);
    }

    @Override
    public TwilioDelegate newDelegate(PluginContext context, Domain domain, PushRequest pushRequest, Push push) {
        TwilioConfig config = getConfig(context.getDatabaseConfig(), domain);
        return new TwilioDelegate(context, domain, pushRequest, (TwilioSmsPush)push, config);
    }

    @Override
    public void deleteConfig(PluginContext context, Domain domain) {

        TwilioConfig config = getConfig(context.getDatabaseConfig(), domain);

        if (config != null) {
            getConfigStore(context.getDatabaseConfig()).delete(config);
            context.setLastMessage("Twilio SMS configuration deleted.");
        } else {
            context.setLastMessage("Twilio SMS configuration doesn't exist.");
        }
    }

    @Override
    public void updateConfig(PluginContext context, Domain domain, MultivaluedMap<String, String> formParams) {
        UpdateTwilioConfigAction action = new UpdateTwilioConfigAction(domain, formParams);

        TwilioConfig twilioConfig = getConfig(context.getDatabaseConfig(), domain);
        if (twilioConfig == null) {
            twilioConfig = new TwilioConfig();
        }

        twilioConfig.apply(action);
        getConfigStore(context.getDatabaseConfig()).update(twilioConfig);

        context.setLastMessage("Twilio configuration updated.");
    }

    @Override
    public void test(PluginContext context, Domain domain) throws Exception {

        TwilioConfig config = getConfig(context.getDatabaseConfig(), domain);

        if (config == null) {
            String msg = "The Twilio config has not been specified.";
            context.setLastMessage(msg);
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
    public String getAdminUi(PluginContext context, Domain domain) throws IOException {

        TwilioConfig config = getConfig(context.getDatabaseConfig(), domain);

        InputStream stream = getClass().getResourceAsStream("/org/tiogasolutions/push/plugins/twilio/admin.html");
        String content = IoUtils.toString(stream);

        content = content.replace("${legend-class}",        nullToString(config == null ? "no-config" : ""));
        content = content.replace("${push-type}",           nullToString(getPushType().getCode()));
        content = content.replace("${domain-key}",          nullToString(domain.getDomainKey()));
        content = content.replace("${context-root}",        PushEnvUtils.findContextRoot());

        content = content.replace("${config-account-sid}",  nullToString(config == null ? null : config.getAccountSid()));
        content = content.replace("${config-auth-token}",   nullToString(config == null ? null : config.getAuthToken()));
        content = content.replace("${config-from-number}",  nullToString(config == null ? null : config.getFromPhoneNumber()));
        content = content.replace("${config-recipient}",    nullToString(config == null ? null : config.getRecipient()));

        if (content.contains("${")) {
            String msg = String.format("The Twilio admin UI still contains un-parsed elements.");
            throw new IllegalStateException(msg);
        }

        return content;
    }
}
