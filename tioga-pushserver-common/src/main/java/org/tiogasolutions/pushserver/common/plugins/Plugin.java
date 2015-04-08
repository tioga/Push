package org.tiogasolutions.pushserver.common.plugins;

import org.tiogasolutions.pushserver.common.AbstractDelegate;
import org.tiogasolutions.pushserver.common.clients.Domain;
import org.tiogasolutions.pushserver.common.requests.PushRequest;
import org.tiogasolutions.pushserver.common.system.CpCouchServer;
import org.tiogasolutions.pushserver.pub.common.Push;
import org.tiogasolutions.pushserver.pub.common.PushType;

import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;

public interface Plugin {

  PushType getPushType();
  PluginConfig getConfig(CpCouchServer couchServer, Domain domain);

  byte[] getIcon(PluginContext context, Domain domain) throws IOException;
  byte[] getEnabledIcon() throws IOException;
  byte[] getDisabledIcon() throws IOException;

  String getAdminUi(PluginContext context, Domain domain) throws IOException;

  AbstractDelegate newDelegate(PluginContext context, Domain domain, PushRequest pushRequest, Push push);
  void test(PluginContext context, Domain domain) throws Exception;
  void updateConfig(PluginContext context, Domain domain, MultivaluedMap<String, String> formParams);
  void deleteConfig(PluginContext context, Domain domain);

}
