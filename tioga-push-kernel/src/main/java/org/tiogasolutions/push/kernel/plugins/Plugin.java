package org.tiogasolutions.push.kernel.plugins;

import org.tiogasolutions.push.kernel.AbstractDelegate;
import org.tiogasolutions.push.kernel.clients.DomainProfileEntity;
import org.tiogasolutions.push.kernel.requests.PushRequest;
import org.tiogasolutions.push.pub.common.Push;
import org.tiogasolutions.push.pub.common.PushType;

import java.io.IOException;
import java.util.Map;

public interface Plugin {

  PushType getPushType();
  PluginConfig getConfig(DomainProfileEntity domainProfile);

  byte[] getIcon(DomainProfileEntity domainProfile) throws IOException;
  byte[] getEnabledIcon() throws IOException;
  byte[] getDisabledIcon() throws IOException;

  String getAdminUi(DomainProfileEntity domainProfile) throws IOException;
  AbstractDelegate newDelegate(DomainProfileEntity domainProfile, PushRequest pushRequest, Push push);
  void test(DomainProfileEntity domainProfile) throws Exception;
  void updateConfig(DomainProfileEntity domainProfile, Map<String, String> params);
  void deleteConfig(DomainProfileEntity domainProfile);

}
