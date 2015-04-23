package org.tiogasolutions.push.plugins.notifier;

import org.tiogasolutions.push.common.PushEnvUtils;
import org.tiogasolutions.push.common.clients.Domain;
import org.tiogasolutions.push.common.plugins.PluginContext;
import org.tiogasolutions.push.common.plugins.PluginSupport;
import org.tiogasolutions.push.common.requests.PushRequest;
import org.tiogasolutions.push.common.system.CpCouchServer;
import org.tiogasolutions.push.common.system.DomainDatabaseConfig;
import org.tiogasolutions.push.pub.common.Push;
import org.tiogasolutions.push.pub.LqNotificationPush;
import org.tiogasolutions.dev.common.IoUtils;

import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import java.io.InputStream;

import static org.tiogasolutions.dev.common.StringUtils.nullToString;

public class LqNotificationsPlugin extends PluginSupport {

  private LqNotificationsConfigStore _configStore;

  public LqNotificationsPlugin() {
    super(LqNotificationPush.PUSH_TYPE);
  }

  public LqNotificationsConfigStore getConfigStore(DomainDatabaseConfig databaseConfig) {
    if (_configStore == null) {
      _configStore = new LqNotificationsConfigStore(databaseConfig);
    }
    return _configStore;
  }

  @Override
  public LqNotificationsConfig getConfig(DomainDatabaseConfig databaseConfig, Domain domain) {
    String docId = LqNotificationsConfigStore.toDocumentId(domain);
    return getConfigStore(databaseConfig).getByDocumentId(docId);
  }

  @Override
  public LqNotificationsDelegate newDelegate(PluginContext context, Domain domain, PushRequest pushRequest, Push push) {
    LqNotificationsConfig config = getConfig(context.getDatabaseConfig(), domain);
    return new LqNotificationsDelegate(context, domain, pushRequest, (LqNotificationPush)push, config);
  }

  @Override
  public void deleteConfig(PluginContext pluginContext, Domain domain) {

    LqNotificationsConfig config = getConfig(pluginContext.getDatabaseConfig(), domain);

    if (config != null) {
      getConfigStore(pluginContext.getDatabaseConfig()).delete(config);
      pluginContext.setLastMessage("Notification configuration deleted.");
    } else {
      pluginContext.setLastMessage("Notification configuration doesn't exist.");
    }
  }

  @Override
  public void updateConfig(PluginContext pluginContext, Domain domain, MultivaluedMap<String, String> formParams) {

    UpdateLqNotificationsConfigAction action = new UpdateLqNotificationsConfigAction(domain, formParams);

    LqNotificationsConfig config = getConfig(pluginContext.getDatabaseConfig(), domain);
    if (config == null) {
      config = new LqNotificationsConfig();
    }

    config.apply(action);
    getConfigStore(pluginContext.getDatabaseConfig()).update(config);

    pluginContext.setLastMessage("Notification configuration updated.");
  }

  @Override
  public void test(PluginContext context, Domain domain) throws Exception {
  }

  @Override
  public String getAdminUi(PluginContext context, Domain domain) throws IOException {

    LqNotificationsConfig config = getConfig(context.getDatabaseConfig(), domain);

    InputStream stream = getClass().getResourceAsStream("/org/tiogasolutions/push/plugins/notifier/admin.html");
    String content = IoUtils.toString(stream);

    content = content.replace("${legend-class}",      nullToString(config == null ? "no-config" : ""));
    content = content.replace("${push-type}",         nullToString(getPushType().getCode()));
    content = content.replace("${plugin-name}",       nullToString(getPluginName()));
    content = content.replace("${domain-key}",        nullToString(domain.getDomainKey()));
    content = content.replace("${context-root}",      PushEnvUtils.findContextRoot());
    content = content.replace("${config-user-name}",  nullToString(config == null ? null : config.getUserName()));

    if (content.contains("${")) {
      String msg = String.format("The Notification admin UI still contains un-parsed elements.");
      throw new IllegalStateException(msg);
    }

    return content;
  }
}
