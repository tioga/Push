package org.tiogasolutions.push.server.grizzly;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.tiogasolutions.apis.bitly.BitlyApis;
import org.tiogasolutions.dev.common.EnvUtils;
import org.tiogasolutions.dev.jackson.TiogaJacksonTranslator;
import org.tiogasolutions.push.jackson.PushObjectMapper;
import org.tiogasolutions.push.kernel.config.CouchServersConfig;
import org.tiogasolutions.push.kernel.execution.ExecutionManager;
import org.tiogasolutions.push.kernel.requests.PushRequestStore;
import org.tiogasolutions.push.kernel.system.PluginManager;
import org.tiogasolutions.push.kernel.system.PushCouchServer;
import org.tiogasolutions.push.kernel.system.SessionStore;
import org.tiogasolutions.push.plugins.ses.SesEmailPlugin;
import org.tiogasolutions.push.plugins.smtp.SmtpEmailPlugin;
import org.tiogasolutions.push.plugins.twilio.TwilioPlugin;
import org.tiogasolutions.push.plugins.xmpp.XmppPlugin;
import org.tiogasolutions.runners.grizzly.GrizzlyServerConfig;

import java.util.Arrays;

@Profile("hosted")
@Configuration
public class PushHostedSpringConfig {

  private String getContext() {
    return EnvUtils.findProperty("push.context", "");
  }

  private int getPort() {
    String value = EnvUtils.findProperty("push.port", "8080");
    return Integer.valueOf(value);
  }

  private int getShutdownPort() {
    String value = EnvUtils.findProperty("push.shutdownPort", "8081");
    return Integer.valueOf(value);
  }

  private String getHostName() {
    return EnvUtils.findProperty("push.hostName", "0.0.0.0");
  }

  private String getMasterUrl() {
    return EnvUtils.requireProperty("push.masterUrl");
  }

  private String getMasterUsername() {
    return EnvUtils.requireProperty("push.masterUsername");
  }

  private String getMasterPassword() {
    return EnvUtils.requireProperty("push.masterPassword");
  }

  private String getMasterDatabaseName() {
    return EnvUtils.requireProperty("push.masterDatabaseName");
  }

  private String getDomainUrl() {
    return EnvUtils.requireProperty("push.domainUrl");
  }

  private String getDomainUsername() {
    return EnvUtils.requireProperty("push.domainUsername");
  }

  private String getDomainPassword() {
    return EnvUtils.requireProperty("push.domainPassword");
  }

  private String getDomainDatabasePrefix() {
    return EnvUtils.requireProperty("push.domainDatabasePrefix");
  }

  private Long getSessionDuration() {
    String value = EnvUtils.requireProperty("push.sessionDuration");
    return Long.valueOf(value);
  }

  @Bean
  public PushObjectMapper pushObjectMapper() {
    return new PushObjectMapper();
  }

  @Bean
  public TiogaJacksonTranslator tiogaJacksonTranslator(PushObjectMapper pushObjectMapper) {
    return new TiogaJacksonTranslator(pushObjectMapper);
  }

  @Bean
  public BitlyApis bitlyApis(TiogaJacksonTranslator tiogaJacksonTranslator) {
    return new BitlyApis(tiogaJacksonTranslator, "9f5ed9c08c695b4a017bfb432eea58876a5d40cb");
  }

  @Bean
  public SessionStore sessionStore() {
    return new SessionStore(getSessionDuration());
  }

  @Bean
  public PushCouchServer pushCouchServer(CouchServersConfig config) {
    return new PushCouchServer(config);
  }

  @Bean
  public ExecutionManager executionManager(CouchServersConfig couchServersConfig, PushCouchServer pushCouchServer) {
    return new ExecutionManager(couchServersConfig, pushCouchServer);
  }

  @Bean
  public PushRequestStore pushRequestStore(ExecutionManager executionManager) {
    return new PushRequestStore(executionManager);
  }

  @Bean
  public PluginManager pluginManager(ExecutionManager executionManager, PushRequestStore pushRequestStore, PushObjectMapper pushObjectMapper, BitlyApis bitlyApis) {
    return new PluginManager(Arrays.asList(
      new XmppPlugin(executionManager, pushObjectMapper, pushRequestStore, bitlyApis),
      new SesEmailPlugin(executionManager, pushObjectMapper, pushRequestStore, bitlyApis),
      new SmtpEmailPlugin(executionManager, pushObjectMapper, pushRequestStore, bitlyApis),
      new TwilioPlugin(executionManager, pushObjectMapper, pushRequestStore, bitlyApis)
    ));
  }

  @Bean
  public GrizzlyServerConfig grizzlyServerConfig() {
    GrizzlyServerConfig config = new GrizzlyServerConfig();
    config.setHostName(getHostName());
    config.setPort(getPort());
    config.setShutdownPort(getShutdownPort());
    config.setContext(getContext());
    config.setToOpenBrowser(false);
    return config;
  }

  @Bean
  public CouchServersConfig couchServersConfig() {
    CouchServersConfig config = new CouchServersConfig();

    config.setMasterUrl(getMasterUrl());
    config.setMasterUsername(getMasterUsername());
    config.setMasterPassword(getMasterPassword());
    config.setMasterDatabaseName(getMasterDatabaseName());

    config.setDomainUrl(getDomainUrl());
    config.setDomainUserName(getDomainUsername());
    config.setDomainPassword(getDomainPassword());
    config.setDomainDatabasePrefix(getDomainDatabasePrefix());

    return config;
  }
}
