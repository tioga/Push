/*
 * Copyright (c) 2014 Jacob D. Parr
 *
 * This software may not be used without permission.
 */

package org.tiogasolutions.push.plugins.twilio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tiogasolutions.dev.common.BeanUtils;
import org.tiogasolutions.dev.common.Formats;
import org.tiogasolutions.dev.common.IoUtils;
import org.tiogasolutions.dev.common.StringUtils;
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

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import static org.tiogasolutions.dev.common.StringUtils.nullToString;

@Component
public class TwilioPlugin extends PluginSupport {

    // private final BitlyApis bitlyApis;

    @Autowired
    public TwilioPlugin(ExecutionManager executionManager, PushObjectMapper objectMapper, PushRequestStore pushRequestStore) {
        super(TwilioSmsPush.PUSH_TYPE, executionManager, objectMapper, pushRequestStore);
        // this.bitlyApis = bitlyApis;
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
        return new TwilioDelegate(executionManager.getContext(), objectMapper, pushRequestStore, pushRequest, (TwilioSmsPush) push, config);
    }

    @Override
    public void deleteConfig(DomainProfileEntity domainProfile) {
        TwilioConfig config = getConfig(domainProfile);

        if (config != null) {
            getConfigStore(executionManager).delete(config);
            executionManager.getContext().setLastMessage("Twilio SMS configuration deleted.");
        } else {
            executionManager.getContext().setLastMessage("Twilio SMS configuration doesn't exist.");
        }
    }

    @Override
    public void updateConfig(DomainProfileEntity domainProfile, Map<String, String> params) {
        UpdateTwilioConfigAction action = new UpdateTwilioConfigAction(domainProfile, params);

        TwilioConfig twilioConfig = getConfig(domainProfile);
        if (twilioConfig == null) {
            twilioConfig = new TwilioConfig();
        }

        twilioConfig.apply(action);
        getConfigStore(executionManager).update(twilioConfig);

        executionManager.getContext().setLastMessage("Twilio configuration updated.");
    }

    @Override
    public void test(DomainProfileEntity domainProfile) throws Exception {

        TwilioConfig config = getConfig(domainProfile);

        if (config == null) {
            String msg = "The Twilio config has not been specified.";
            executionManager.getContext().setLastMessage(msg);
            return;
        }

        String when = Formats.defaultStamp(new java.util.Date());
        TwilioSmsPush push = TwilioSmsPush.newPush(
                config.getTestFromNumber(), config.getTestToNumber(),
                String.format("Twilio test message from Cosmic Push sent at %s.", when),
                null, BeanUtils.toMap("twilio-test:true"));

        PushRequest pushRequest = new PushRequest(Push.CURRENT_API_VERSION, domainProfile, push);
        pushRequestStore.create(pushRequest);

        if (new TwilioDelegate(executionManager.getContext(), objectMapper, pushRequestStore, pushRequest, push, config).execute(false)) {
            String msg = String.format("Test message sent from %s to %s:\n%s", config.getTestFromNumber(), config.getTestToNumber(), push.getMessage());
            executionManager.getContext().setLastMessage(msg);
        }
        ;
    }

    @Override
    public String getAdminUi(DomainProfileEntity domainProfile) throws IOException {
        ExecutionContext context = executionManager.getContext();
        String contextRoot = KernelUtils.getContextRoot(context.getUriInfo());

        TwilioConfig config = getConfig(domainProfile);

        InputStream stream = getClass().getResourceAsStream("/org/tiogasolutions/push/plugins/twilio/admin.html");
        String content = IoUtils.toString(stream);

        content = content.replace("${legend-class}", nullToString(config == null ? "no-config" : ""));
        content = content.replace("${push-type}", nullToString(getPushType().getCode()));
        content = content.replace("${domain-key}", nullToString(domainProfile.getDomainKey()));
        content = content.replace("${context-root}", nullToString(contextRoot));

        content = content.replace("${config-account-sid}", nullToString(config == null ? null : config.getAccountSid()));
        content = content.replace("${config-auth-token}", nullToString(config == null ? null : config.getAuthToken()));
        content = content.replace("${config-test-from-number}", nullToString(config == null ? null : config.getTestFromNumber()));
        content = content.replace("${config-test-to-number}", nullToString(config == null ? null : config.getTestToNumber()));

        int index = content.indexOf("${");
        if (index >= 0) {
            String msg = String.format("The Twilio admin UI still contains un-parsed elements: %s",
                    StringUtils.substring(content, index, content.indexOf("}", index)+1));
            throw new IllegalStateException(msg);
        }

        return content;
    }
}
