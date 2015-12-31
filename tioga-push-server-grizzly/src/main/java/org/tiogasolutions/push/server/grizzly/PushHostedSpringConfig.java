package org.tiogasolutions.push.server.grizzly;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.tiogasolutions.apis.bitly.BitlyApis;
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

  @Value("#{systemEnvironment.context}")
  private String context;

  @Value("#{systemEnvironment.port}")
  private int port = 8080;

  @Value("#{systemEnvironment.shutdownPort}")
  private int shutdownPort = 8081;

  @Value("#{systemEnvironment.hostName}")
  private String hostName = "0.0.0.0";

  @Value("#{systemEnvironment.masterUrl}")
  private String masterUrl;

  @Value("#{systemEnvironment.masterUsername}")
  private String masterUsername;

  @Value("#{systemEnvironment.masterPassword}")
  private String masterPassword;

  @Value("#{systemEnvironment.masterDatabaseName}")
  private String masterDatabaseName;

  @Value("#{systemEnvironment.domainUrl}")
  private String domainUrl;

  @Value("#{systemEnvironment.domainUsername}")
  private String domainUsername;

  @Value("#{systemEnvironment.domainPassword}")
  private String domainPassword;

  @Value("#{systemEnvironment.domainDatabasePrefix}")
  private String domainDatabasePrefix;

  @Value("#{systemEnvironment.sessionDuration}")
  private long sessionDuration;

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
    return new SessionStore(sessionDuration);
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
    config.setHostName(hostName);
    config.setPort(port);
    config.setShutdownPort(shutdownPort);
    config.setContext(context);
    config.setToOpenBrowser(false);
    return config;
  }

  @Bean
  public CouchServersConfig couchServersConfig() {
    CouchServersConfig config = new CouchServersConfig();

    config.setMasterUrl(masterUrl);
    config.setMasterUsername(masterUsername);
    config.setMasterPassword(masterPassword);
    config.setMasterDatabaseName(masterDatabaseName);

    config.setDomainUrl(domainUrl);
    config.setDomainUserName(domainUsername);
    config.setDomainPassword(domainPassword);
    config.setDomainDatabasePrefix(domainDatabasePrefix);

    return config;
  }
}
