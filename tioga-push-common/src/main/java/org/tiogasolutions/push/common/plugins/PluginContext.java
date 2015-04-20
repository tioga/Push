package org.tiogasolutions.push.common.plugins;

import org.tiogasolutions.push.common.requests.PushRequestStore;
import org.tiogasolutions.push.common.system.CpCouchServer;
import org.tiogasolutions.push.common.accounts.DomainStore;
import org.tiogasolutions.push.common.system.AppContext;
import org.tiogasolutions.push.common.system.DomainDatabaseConfig;
import org.tiogasolutions.push.jackson.CpObjectMapper;

import java.net.URI;

public interface PluginContext {

  PushRequestStore getPushRequestStore();
  DomainStore getDomainStore();

  CpObjectMapper getObjectMapper();

  DomainDatabaseConfig getDatabaseConfig();

  PushProcessor getPushProcessor();

  URI getBaseURI();

  AppContext getAppContext();

  void setLastMessage(String message);
}
