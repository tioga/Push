package org.tiogasolutions.push.common.plugins;

import org.tiogasolutions.push.common.AbstractDelegate;
import org.tiogasolutions.push.common.system.CpCouchServer;
import org.tiogasolutions.push.common.clients.Domain;
import org.tiogasolutions.push.common.requests.PushRequest;
import org.tiogasolutions.push.common.system.DomainDatabaseConfig;
import org.tiogasolutions.push.pub.common.Push;
import org.tiogasolutions.push.pub.common.PushType;

import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;

public interface Plugin {

  PushType getPushType();
  PluginConfig getConfig(DomainDatabaseConfig databaseConfig, Domain domain);

  byte[] getIcon(PluginContext context, Domain domain) throws IOException;
  byte[] getEnabledIcon() throws IOException;
  byte[] getDisabledIcon() throws IOException;

  String getAdminUi(PluginContext context, Domain domain) throws IOException;

  AbstractDelegate newDelegate(PluginContext context, Domain domain, PushRequest pushRequest, Push push);
  void test(PluginContext context, Domain domain) throws Exception;
  void updateConfig(PluginContext context, Domain domain, MultivaluedMap<String, String> formParams);
  void deleteConfig(PluginContext context, Domain domain);

}
