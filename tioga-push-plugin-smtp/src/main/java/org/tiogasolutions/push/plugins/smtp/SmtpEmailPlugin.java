package org.tiogasolutions.push.plugins.smtp;

import org.tiogasolutions.dev.common.BeanUtils;
import org.tiogasolutions.dev.common.Formats;
import org.tiogasolutions.dev.common.IoUtils;
import org.tiogasolutions.dev.common.StringUtils;
import org.tiogasolutions.push.kernel.KernelUtils;
import org.tiogasolutions.push.kernel.clients.DomainProfileEntity;
import org.tiogasolutions.push.kernel.execution.ExecutionContext;
import org.tiogasolutions.push.kernel.execution.ExecutionManager;
import org.tiogasolutions.push.kernel.plugins.PluginSupport;
import org.tiogasolutions.push.kernel.requests.PushRequest;
import org.tiogasolutions.push.pub.SmtpEmailPush;
import org.tiogasolutions.push.pub.common.Push;

import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import java.io.InputStream;

import static org.tiogasolutions.dev.common.StringUtils.nullToString;

public class SmtpEmailPlugin extends PluginSupport {

  private SmtpEmailConfigStore _configStore;

  public SmtpEmailPlugin(ExecutionManager executionManager) {
    super(SmtpEmailPush.PUSH_TYPE, executionManager);
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
    return new SmtpEmailDelegate(executionManager.context(), pushRequest, (SmtpEmailPush)push, config);
  }

  @Override
  public void updateConfig(DomainProfileEntity domainProfile, MultivaluedMap<String, String> formParams) {
    UpdateSmtpEmailConfigAction action = new UpdateSmtpEmailConfigAction(domainProfile, formParams);

    SmtpEmailConfig smtpEmailConfig = getConfig(domainProfile);
    if (smtpEmailConfig == null) {
      smtpEmailConfig = new SmtpEmailConfig();
    }

    smtpEmailConfig.apply(action);
    getConfigStore(executionManager).update(smtpEmailConfig);

    executionManager.context().setLastMessage("SMTP Email configuration updated.");
  }

  @Override
  public void deleteConfig(DomainProfileEntity domainProfile) {
    SmtpEmailConfig config = getConfig(domainProfile);

    if (config != null) {
      getConfigStore(executionManager).delete(config);
      executionManager.context().setLastMessage("SMTP email configuration deleted.");
    } else {
      executionManager.context().setLastMessage("SMTP email configuration doesn't exist.");
    }
  }

  @Override
  public void test(DomainProfileEntity domainProfile) throws Exception {
    SmtpEmailConfig config = getConfig(domainProfile);

    if (config == null) {
      String msg = "The SMTP email config has not been specified.";
      executionManager.context().setLastMessage(msg);
      return;
    }

    String toAddress = config.getTestToAddress();
    String fromAddress = config.getTestFromAddress();

    if (StringUtils.isBlank((toAddress))) {
      String msg = "A test message cannot be sent with out specifying the config's test-to-address.";
      executionManager.context().setLastMessage(msg);
      return;
    }

    if (StringUtils.isBlank((fromAddress))) {
      String msg = "A test message cannot be sent with out specifying the config's test-from-address.";
      executionManager.context().setLastMessage(msg);
      return;
    }

    String override = config.getRecipientOverride();
    if (StringUtils.isNotBlank(override)) {
      toAddress = override;
    }

    String when = Formats.defaultStamp(new java.util.Date());
    String msg = String.format("<html><head><title>Some Email</title></head><body style='background-color:red'><div style='background-color:#c0c0ff'><h1>Testing 123</h1>This is a test message from Cosmic Push sent at %s.</div></body>", when);
    String subject = "SMTP Test message from Cosmic Push";
    SmtpEmailPush push = SmtpEmailPush.newPush(
        toAddress, fromAddress,
        subject, msg,
        null, BeanUtils.toMap("smtp-test:true"));

    PushRequest pushRequest = new PushRequest(Push.CURRENT_API_VERSION, domainProfile, push);
    executionManager.context().getPushRequestStore().create(pushRequest);

    new SmtpEmailDelegate(executionManager.context(), pushRequest, push, config).run();

    msg = String.format("Test message sent from %s to %s", fromAddress, toAddress);
    executionManager.context().setLastMessage(msg);
  }

  @Override
  public String getAdminUi(DomainProfileEntity domainProfile) throws IOException {
    ExecutionContext context = executionManager.context();
    String contextRoot = KernelUtils.getContextRoot(context.getUriInfo());

    SmtpEmailConfig config = getConfig(domainProfile);

    InputStream stream = getClass().getResourceAsStream("/org/tiogasolutions/push/plugins/smtp/admin.html");
    String content = IoUtils.toString(stream);

    content = content.replace("${legend-class}",              nullToString(config == null ? "no-config" : ""));
    content = content.replace("${push-type}",                 nullToString(getPushType().getCode()));
    content = content.replace("${plugin-name}",               nullToString(getPluginName()));
    content = content.replace("${domain-key}",                nullToString(domainProfile.getDomainKey()));
    content = content.replace("${context-root}",              nullToString(contextRoot));
    content = content.replace("${config-user-name}",          nullToString(config == null ? null : config.getUserName()));
    content = content.replace("${config-password}",           nullToString(config == null ? null : config.getPassword()));
    content = content.replace("${config-auth-type}",          nullToString(config == null ? null : config.getAuthType()));
    content = content.replace("${config-port-number}",        nullToString(config == null ? null : config.getPortNumber()));
    content = content.replace("${config-server-name}",        nullToString(config == null ? null : config.getServerName()));
    content = content.replace("${config-test-to-address}",    nullToString(config == null ? null : config.getTestToAddress()));
    content = content.replace("${config-test-from-address}",  nullToString(config == null ? null : config.getTestFromAddress()));
    content = content.replace("${config-recipient-override}", nullToString(config == null ? null : config.getRecipientOverride()));

    if (content.contains("${")) {
      String msg = "The SMTP admin UI still contains un-parsed elements.";
      throw new IllegalStateException(msg);
    }

    return content;
  }
}
