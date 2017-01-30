package org.tiogasolutions.push.plugins.ses;

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
import org.tiogasolutions.push.pub.SesEmailPush;
import org.tiogasolutions.push.pub.common.Push;

import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import java.io.InputStream;

import static org.tiogasolutions.dev.common.StringUtils.nullToString;

@Component
public class SesEmailPlugin extends PluginSupport {

    // private final BitlyApis bitlyApis;

    @Autowired
    public SesEmailPlugin(ExecutionManager executionManager, PushObjectMapper objectMapper, PushRequestStore pushRequestStore) {
        super(SesEmailPush.PUSH_TYPE, executionManager, objectMapper, pushRequestStore);
        // this.bitlyApis = bitlyApis;
    }

    public SesEmailConfigStore getConfigStore() {
        return new SesEmailConfigStore(executionManager);
    }

    @Override
    public SesEmailConfig getConfig(DomainProfileEntity domainProfile) {
        String docId = SesEmailConfigStore.toDocumentId(domainProfile);
        return getConfigStore().getByDocumentId(docId);
    }

    @Override
    public SesEmailDelegate newDelegate(DomainProfileEntity domainProfile, PushRequest pushRequest, Push push) {
        SesEmailConfig config = getConfig(domainProfile);
        return new SesEmailDelegate(executionManager.getContext(), objectMapper, pushRequestStore, /*bitlyApis,*/ pushRequest, (SesEmailPush) push, config);
    }

    @Override
    public void deleteConfig(DomainProfileEntity domainProfile) {
        SesEmailConfig config = getConfig(domainProfile);

        if (config != null) {
            getConfigStore().delete(config);
            executionManager.getContext().setLastMessage("SES email configuration deleted.");
        } else {
            executionManager.getContext().setLastMessage("SES email configuration doesn't exist.");
        }
    }

    @Override
    public void updateConfig(DomainProfileEntity domainProfile, MultivaluedMap<String, String> formParams) {
        UpdateSesEmailConfigAction action = new UpdateSesEmailConfigAction(domainProfile, formParams);

        SesEmailConfig sesEmailConfig = getConfig(domainProfile);
        if (sesEmailConfig == null) {
            sesEmailConfig = new SesEmailConfig();
        }

        sesEmailConfig.apply(action);
        getConfigStore().update(sesEmailConfig);

        executionManager.getContext().setLastMessage("SES Email configuration updated.");
    }

    @Override
    public void test(DomainProfileEntity domainProfile) throws Exception {
        SesEmailConfig config = getConfig(domainProfile);

        if (config == null) {
            String msg = "The SES Email config has not been specified.";
            executionManager.getContext().setLastMessage(msg);
            return;
        }

        String toAddress = config.getTestToAddress();
        String fromAddress = config.getTestFromAddress();

        if (StringUtils.isBlank((toAddress))) {
            String msg = "A test message cannot be sent with out specifying the config's test-to-address.";
            executionManager.getContext().setLastMessage(msg);
            return;
        }

        if (StringUtils.isBlank((fromAddress))) {
            String msg = "A test message cannot be sent with out specifying the config's test-from-address.";
            executionManager.getContext().setLastMessage(msg);
            return;
        }

        String override = config.getRecipientOverride();
        if (StringUtils.isNotBlank(override)) {
            toAddress = override;
        }

        String when = Formats.defaultStamp(new java.util.Date());
        SesEmailPush push = SesEmailPush.newPush(toAddress, fromAddress,
                "ASES test message from Cosmic Push",
                String.format("<html><head><title>Some Email</title></head><body style='background-color:red'><div style='background-color:#c0c0ff'><h1>Testing 123</h1>This is a test message from Cosmic Push sent at %s.</div></body>", when),
                null, BeanUtils.toMap("ases-test:true"));

        PushRequest pushRequest = new PushRequest(Push.CURRENT_API_VERSION, domainProfile, push);
        pushRequestStore.create(pushRequest);

        if (new SesEmailDelegate(executionManager.getContext(), objectMapper, pushRequestStore, /*bitlyApis,*/ pushRequest, push, config).execute(false)) {
            String msg = String.format("Test message sent from %s to %s\n%s", fromAddress, toAddress, push.getEmailSubject());
            executionManager.getContext().setLastMessage(msg);
        }
    }

    @Override
    public String getAdminUi(DomainProfileEntity domainProfile) throws IOException {
        ExecutionContext context = executionManager.getContext();
        String contextRoot = KernelUtils.getContextRoot(context.getUriInfo());

        SesEmailConfig config = getConfig(domainProfile);

        InputStream stream = getClass().getResourceAsStream("/org/tiogasolutions/push/plugins/ses/admin.html");
        String content = IoUtils.toString(stream);

        content = content.replace("${legend-class}", nullToString(config == null ? "no-config" : ""));
        content = content.replace("${push-type}", nullToString(getPushType().getCode()));
        content = content.replace("${plugin-name}", nullToString(getPluginName()));
        content = content.replace("${domain-key}", nullToString(domainProfile.getDomainKey()));
        content = content.replace("${context-root}", nullToString(contextRoot));
        content = content.replace("${config-access-key-id}", nullToString(config == null ? null : config.getAccessKeyId()));
        content = content.replace("${config-secret-key}", nullToString(config == null ? null : config.getSecretKey()));
        content = content.replace("${config-endpoint}", nullToString(config == null ? null : config.getEndpoint()));
        content = content.replace("${config-test-to-address}", nullToString(config == null ? null : config.getTestToAddress()));
        content = content.replace("${config-test-from-address}", nullToString(config == null ? null : config.getTestFromAddress()));
        content = content.replace("${config-recipient-override}", nullToString(config == null ? null : config.getRecipientOverride()));

        int index = content.indexOf("${");
        if (index >= 0) {
            String msg = String.format("The SES admin UI still contains un-parsed elements: %s",
                    StringUtils.substring(content, index, content.indexOf("}", index) + 1));
            throw new IllegalStateException(msg);
        }

        return content;
    }
}
