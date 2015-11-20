package org.tiogasolutions.push.plugins.ses;

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
import org.tiogasolutions.push.pub.SesEmailPush;
import org.tiogasolutions.push.pub.common.Push;

import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import java.io.InputStream;

import static org.tiogasolutions.dev.common.StringUtils.nullToString;

public class SesEmailPlugin extends PluginSupport {

  public SesEmailPlugin(ExecutionManager executionManager) {
    super(SesEmailPush.PUSH_TYPE, executionManager);
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
    return new SesEmailDelegate(executionManager.context(), pushRequest, (SesEmailPush)push, config);
  }

  @Override
  public void deleteConfig(DomainProfileEntity domainProfile) {
    SesEmailConfig config = getConfig(domainProfile);

    if (config != null) {
      getConfigStore().delete(config);
      executionManager.context().setLastMessage("SES email configuration deleted.");
    } else {
      executionManager.context().setLastMessage("SES email configuration doesn't exist.");
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

    executionManager.context().setLastMessage("SES Email configuration updated.");
  }

  @Override
  public void test(DomainProfileEntity domainProfile) throws Exception {
    SesEmailConfig config = getConfig(domainProfile);

    if (config == null) {
      String msg = "The SES Email config has not been specified.";
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
    String subject = "ASES test message from Cosmic Push";
    SesEmailPush push = SesEmailPush.newPush(toAddress, fromAddress, subject, msg, null, BeanUtils.toMap("ases-test:true"));

    PushRequest pushRequest = new PushRequest(Push.CURRENT_API_VERSION, domainProfile, push);
    executionManager.context().getPushRequestStore().create(pushRequest);

    new SesEmailDelegate(executionManager.context(), pushRequest, push, config).run();

    msg = String.format("Test message sent from %s to %s", fromAddress, toAddress);
    executionManager.context().setLastMessage(msg);
  }

  @Override
  public String getAdminUi(DomainProfileEntity domainProfile) throws IOException {
    ExecutionContext context = executionManager.context();
    String contextRoot = KernelUtils.getContextRoot(context.getUriInfo());

    SesEmailConfig config = getConfig(domainProfile);

    InputStream stream = getClass().getResourceAsStream("/org/tiogasolutions/push/plugins/ses/admin.html");
    String content = IoUtils.toString(stream);

    content = content.replace("${legend-class}",              nullToString(config == null ? "no-config" : ""));
    content = content.replace("${push-type}",                 nullToString(getPushType().getCode()));
    content = content.replace("${plugin-name}",               nullToString(getPluginName()));
    content = content.replace("${domain-key}",                nullToString(domainProfile.getDomainKey()));
    content = content.replace("${context-root}",              nullToString(contextRoot));
    content = content.replace("${config-access-key-id}",      nullToString(config == null ? null : config.getAccessKeyId()));
    content = content.replace("${config-secret-key}",         nullToString(config == null ? null : config.getSecretKey()));
    content = content.replace("${config-endpoint}",           nullToString(config == null ? null : config.getEndpoint()));
    content = content.replace("${config-test-to-address}",    nullToString(config == null ? null : config.getTestToAddress()));
    content = content.replace("${config-test-from-address}",  nullToString(config == null ? null : config.getTestFromAddress()));
    content = content.replace("${config-recipient-override}", nullToString(config == null ? null : config.getRecipientOverride()));

    if (content.contains("${")) {
      String msg = "The SES admin UI still contains un-parsed elements";
      throw new IllegalStateException(msg);
    }

    return content;
  }
}
