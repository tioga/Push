package org.tiogasolutions.pushserver.common.plugins;

import org.tiogasolutions.pushserver.common.accounts.DomainStore;
import org.tiogasolutions.pushserver.common.requests.PushRequestStore;
import org.tiogasolutions.pushserver.common.system.AppContext;
import org.tiogasolutions.pushserver.common.system.CpCouchServer;
import org.tiogasolutions.pushserver.jackson.CpObjectMapper;

import java.net.URI;

public interface PluginContext {

  PushRequestStore getPushRequestStore();
  DomainStore getDomainStore();

  CpObjectMapper getObjectMapper();

  CpCouchServer getCouchServer();

  PushProcessor getPushProcessor();

  URI getBaseURI();

  AppContext getAppContext();

  void setLastMessage(String message);
}
