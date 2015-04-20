package org.tiogasolutions.push.plugins.ses;

import org.tiogasolutions.push.common.clients.Domain;
import org.tiogasolutions.push.common.plugins.PluginContext;
import org.tiogasolutions.push.common.plugins.PluginSupport;
import org.tiogasolutions.push.common.requests.PushRequest;
import org.tiogasolutions.push.common.system.AppContext;
import org.tiogasolutions.push.common.system.CpCouchServer;
import org.tiogasolutions.push.common.system.DomainDatabaseConfig;
import org.tiogasolutions.push.pub.common.Push;
import org.tiogasolutions.push.pub.SesEmailPush;
import org.tiogasolutions.dev.common.BeanUtils;
import org.tiogasolutions.dev.common.Formats;
import org.tiogasolutions.dev.common.IoUtils;
import org.tiogasolutions.dev.common.StringUtils;

import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import java.io.InputStream;

import static org.tiogasolutions.dev.common.StringUtils.nullToString;

public class SesEmailPlugin extends PluginSupport {

  private SesEmailConfigStore _configStore;

  public SesEmailPlugin() {
    super(SesEmailPush.PUSH_TYPE);
  }

  public SesEmailConfigStore getConfigStore(DomainDatabaseConfig databaseConfig) {
    if (_configStore == null) {
      _configStore = new SesEmailConfigStore(databaseConfig);
    }
    return _configStore;
  }

  @Override
  public SesEmailConfig getConfig(DomainDatabaseConfig databaseConfig, Domain domain) {
    String docId = SesEmailConfigStore.toDocumentId(domain);
    return getConfigStore(databaseConfig).getByDocumentId(docId);
  }

  @Override
  public SesEmailDelegate newDelegate(PluginContext context, Domain domain, PushRequest pushRequest, Push push) {
    SesEmailConfig config = getConfig(context.getDatabaseConfig(), domain);
    return new SesEmailDelegate(context, domain, pushRequest, (SesEmailPush)push, config);
  }

  @Override
  public void deleteConfig(PluginContext pluginContext, Domain domain) {

    SesEmailConfig config = getConfig(pluginContext.getDatabaseConfig(), domain);

    if (config != null) {
      getConfigStore(pluginContext.getDatabaseConfig()).delete(config);
      pluginContext.setLastMessage("SES email configuration deleted.");
    } else {
      pluginContext.setLastMessage("SES email configuration doesn't exist.");
    }
  }

  @Override
  public void updateConfig(PluginContext pluginContext, Domain domain, MultivaluedMap<String, String> formParams) {

    UpdateSesEmailConfigAction action = new UpdateSesEmailConfigAction(domain, formParams);

    SesEmailConfig sesEmailConfig = getConfig(pluginContext.getDatabaseConfig(), domain);
    if (sesEmailConfig == null) {
      sesEmailConfig = new SesEmailConfig();
    }

    sesEmailConfig.apply(action);
    getConfigStore(pluginContext.getDatabaseConfig()).update(sesEmailConfig);

    pluginContext.setLastMessage("SES Email configuration updated.");
  }

  @Override
  public void test(PluginContext pluginContext, Domain domain) throws Exception {

    SesEmailConfig config = getConfig(pluginContext.getDatabaseConfig(), domain);

    if (config == null) {
      String msg = "The SES Email config has not been specified.";
      pluginContext.setLastMessage(msg);
      return;
    }

    String toAddress = config.getTestToAddress();
    String fromAddress = config.getTestFromAddress();

    if (StringUtils.isBlank((toAddress))) {
      String msg = "A test message cannot be sent with out specifying the config's test-to-address.";
      pluginContext.setLastMessage(msg);
      return;
    }

    if (StringUtils.isBlank((fromAddress))) {
      String msg = "A test message cannot be sent with out specifying the config's test-from-address.";
      pluginContext.setLastMessage(msg);
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

    PushRequest pushRequest = new PushRequest(AppContext.CURRENT_API_VERSION, domain, push);
    pluginContext.getPushRequestStore().create(pushRequest);

    new SesEmailDelegate(pluginContext, domain, pushRequest, push, config).run();

    msg = String.format("Test message sent from %s to %s", fromAddress, toAddress);
    pluginContext.setLastMessage(msg);
  }

  @Override
  public String getAdminUi(PluginContext context, Domain domain) throws IOException {

    SesEmailConfig config = getConfig(context.getDatabaseConfig(), domain);

    InputStream stream = getClass().getResourceAsStream("/org/tiogasolutions/push/plugins/ses/admin.html");
    String content = IoUtils.toString(stream);

    content = content.replace("${legend-class}",              nullToString(config == null ? "no-config" : ""));
    content = content.replace("${push-type}",                 nullToString(getPushType().getCode()));
    content = content.replace("${plugin-name}",               nullToString(getPluginName()));
    content = content.replace("${domain-key}",                nullToString(domain.getDomainKey()));
    content = content.replace("${push-server-base}",          nullToString(context.getBaseURI()));
    content = content.replace("${config-access-key-id}",      nullToString(config == null ? null : config.getAccessKeyId()));
    content = content.replace("${config-secret-key}",         nullToString(config == null ? null : config.getSecretKey()));
    content = content.replace("${config-endpoint}",           nullToString(config == null ? null : config.getEndpoint()));
    content = content.replace("${config-test-to-address}",    nullToString(config == null ? null : config.getTestToAddress()));
    content = content.replace("${config-test-from-address}",  nullToString(config == null ? null : config.getTestFromAddress()));
    content = content.replace("${config-recipient-override}", nullToString(config == null ? null : config.getRecipientOverride()));

    if (content.contains("${")) {
      String msg = String.format("The SES admin UI still contains un-parsed elements.");
      throw new IllegalStateException(msg);
    }

    return content;
  }
}
