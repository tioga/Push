package org.tiogasolutions.push.plugins.smtp;

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
import org.tiogasolutions.push.pub.SmtpEmailPush;
import org.tiogasolutions.push.pub.common.Push;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import static org.tiogasolutions.dev.common.StringUtils.nullToString;

@Component
public class SmtpEmailPlugin extends PluginSupport {

    private SmtpEmailConfigStore _configStore;
    // private final BitlyApis bitlyApis;

    @Autowired
    public SmtpEmailPlugin(ExecutionManager executionManager, PushObjectMapper objectMapper, PushRequestStore pushRequestStore) {
        super(SmtpEmailPush.PUSH_TYPE, executionManager, objectMapper, pushRequestStore);
        // this.bitlyApis = bitlyApis;
    }

    public SmtpEmailConfigStore getConfigStore(ExecutionManager executionManager) {
        if (_configStore == null) {
            _configStore = new SmtpEmailConfigStore(executionManager);
        }
        return _configStore;
    }

    @Override
    public SmtpEmailConfig getConfig(DomainProfileEntity domainProfile) {
        String docId = SmtpEmailConfigStore.toDocumentId(domainProfile);
        return getConfigStore(executionManager).getByDocumentId(docId);
    }

    @Override
    public SmtpEmailDelegate newDelegate(DomainProfileEntity domainProfile, PushRequest pushRequest, Push push) {
        SmtpEmailConfig config = getConfig(domainProfile);
        return new SmtpEmailDelegate(executionManager.getContext(), objectMapper, pushRequestStore, /*bitlyApis,*/ pushRequest, (SmtpEmailPush) push, config);
    }

    @Override
    public void updateConfig(DomainProfileEntity domainProfile, Map<String, String> params) {
        UpdateSmtpEmailConfigAction action = new UpdateSmtpEmailConfigAction(domainProfile, params);

        SmtpEmailConfig smtpEmailConfig = getConfig(domainProfile);
        if (smtpEmailConfig == null) {
            smtpEmailConfig = new SmtpEmailConfig();
        }

        smtpEmailConfig.apply(action);
        getConfigStore(executionManager).update(smtpEmailConfig);

        executionManager.getContext().setLastMessage("SMTP Email configuration updated.");
    }

    @Override
    public void deleteConfig(DomainProfileEntity domainProfile) {
        SmtpEmailConfig config = getConfig(domainProfile);

        if (config != null) {
            getConfigStore(executionManager).delete(config);
            executionManager.getContext().setLastMessage("SMTP email configuration deleted.");
        } else {
            executionManager.getContext().setLastMessage("SMTP email configuration doesn't exist.");
        }
    }

    @Override
    public void test(DomainProfileEntity domainProfile) throws Exception {
        SmtpEmailConfig config = getConfig(domainProfile);

        if (config == null) {
            String msg = "The SMTP email config has not been specified.";
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
        SmtpEmailPush push = SmtpEmailPush.newPush(
                toAddress, fromAddress,
                "SMTP Test message from Cosmic Push",
                String.format("<html><head><title>Some Email</title></head><body style='background-color:red'><div style='background-color:#c0c0ff'><h1>Testing 123</h1>This is a test message from Cosmic Push sent at %s.</div></body>", when),
                null, BeanUtils.toMap("smtp-test:true"));

        PushRequest pushRequest = new PushRequest(Push.CURRENT_API_VERSION, domainProfile, push);
        pushRequestStore.create(pushRequest);

        if (new SmtpEmailDelegate(executionManager.getContext(), objectMapper, pushRequestStore, /*bitlyApis,*/ pushRequest, push, config).execute(false)) {
            String msg = String.format("Test message sent from %s to %s\n%s", fromAddress, toAddress, push.getEmailSubject());
            executionManager.getContext().setLastMessage(msg);
        }
        ;
    }

    @Override
    public String getAdminUi(DomainProfileEntity domainProfile) throws IOException {
        ExecutionContext context = executionManager.getContext();
        String contextRoot = KernelUtils.getContextRoot(context.getUriInfo());

        SmtpEmailConfig config = getConfig(domainProfile);

        InputStream stream = getClass().getResourceAsStream("/org/tiogasolutions/push/plugins/smtp/admin.html");
        String content = IoUtils.toString(stream);

        content = content.replace("${legend-class}", nullToString(config == null ? "no-config" : ""));
        content = content.replace("${push-type}", nullToString(getPushType().getCode()));
        content = content.replace("${plugin-name}", nullToString(getPluginName()));
        content = content.replace("${domain-key}", nullToString(domainProfile.getDomainKey()));
        content = content.replace("${context-root}", nullToString(contextRoot));
        content = content.replace("${config-user-name}", nullToString(config == null ? null : config.getUsername()));
        content = content.replace("${config-password}", nullToString(config == null ? null : config.getPassword()));
        content = content.replace("${config-auth-type}", nullToString(config == null ? null : config.getAuthType()));
        content = content.replace("${config-port}", nullToString(config == null ? null : config.getPort()));
        content = content.replace("${config-server-name}", nullToString(config == null ? null : config.getServerName()));
        content = content.replace("${config-test-to-address}", nullToString(config == null ? null : config.getTestToAddress()));
        content = content.replace("${config-test-from-address}", nullToString(config == null ? null : config.getTestFromAddress()));
        content = content.replace("${config-recipient-override}", nullToString(config == null ? null : config.getRecipientOverride()));

        int index = content.indexOf("${");
        if (index >= 0) {
            String msg = String.format("The SMTP admin UI still contains un-parsed elements: %s",
                    StringUtils.substring(content, index, content.indexOf("}", index)+1));
            throw new IllegalStateException(msg);
        }

        return content;
    }
}
