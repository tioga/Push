package org.tiogasolutions.push.server.grizzly;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.tiogasolutions.dev.jackson.TiogaJacksonTranslator;
import org.tiogasolutions.push.engine.system.PushApplication;
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
import org.tiogasolutions.runners.grizzly.GrizzlyServer;
import org.tiogasolutions.runners.grizzly.GrizzlyServerConfig;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static org.tiogasolutions.dev.common.EnvUtils.findProperty;
import static org.tiogasolutions.dev.common.EnvUtils.requireProperty;

@Profile("hosted")
@Configuration
public class PushHostedSpringConfig {

  @Bean
  public PushObjectMapper pushObjectMapper() {
    return new PushObjectMapper();
  }

  @Bean
  public TiogaJacksonTranslator tiogaJacksonTranslator(PushObjectMapper pushObjectMapper) {
    return new TiogaJacksonTranslator(pushObjectMapper);
  }

  @Bean
  public SessionStore sessionStore() {
    String defaultValue = String.valueOf(TimeUnit.MINUTES.toMillis(60));
    String value = findProperty("push.sessionDuration", defaultValue );
    long duration = Long.valueOf(value);
    return new SessionStore(duration);
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
  public PluginManager pluginManager(ExecutionManager executionManager, PushRequestStore pushRequestStore, PushObjectMapper pushObjectMapper) {
    return new PluginManager(Arrays.asList(
      new XmppPlugin(executionManager, pushObjectMapper, pushRequestStore),
      new SesEmailPlugin(executionManager, pushObjectMapper, pushRequestStore),
      new SmtpEmailPlugin(executionManager, pushObjectMapper, pushRequestStore),
      new TwilioPlugin(executionManager, pushObjectMapper, pushRequestStore)
    ));
  }

  @Bean
  public GrizzlyServerConfig grizzlyServerConfig() {
    GrizzlyServerConfig config = new GrizzlyServerConfig();
    config.setHostName(findProperty("push.hostName", "0.0.0.0"));
    config.setPort(Integer.valueOf(findProperty("push.port", "39009")));
    config.setShutdownPort(Integer.valueOf(findProperty("push.shutdownPort", "39010")));
    config.setContext(findProperty("push.context", ""));
    config.setToOpenBrowser(false);
    return config;
  }

  @Bean
  public CouchServersConfig couchServersConfig() {
    CouchServersConfig config = new CouchServersConfig();

    config.setMasterUrl(requireProperty("push.masterUrl"));
    config.setMasterUsername(requireProperty("push.masterUsername"));
    config.setMasterPassword(requireProperty("push.masterPassword"));
    config.setMasterDatabaseName(requireProperty("push.masterDatabaseName"));

    config.setDomainUrl(requireProperty("push.domainUrl"));
    config.setDomainUserName(requireProperty("push.domainUsername"));
    config.setDomainPassword(requireProperty("push.domainPassword"));
    config.setDomainDatabasePrefix(requireProperty("push.domainDatabasePrefix"));

    return config;
  }

  @Bean
  public GrizzlyServer grizzlyServer(GrizzlyServerConfig grizzlyServerConfig, PushApplication application, ApplicationContext applicationContext) {

    ResourceConfig resourceConfig = ResourceConfig.forApplication(application);
    resourceConfig.property("contextConfig", applicationContext);
    resourceConfig.packages("org.tiogasolutions.push");

    return new GrizzlyServer(grizzlyServerConfig, resourceConfig);
  }
}
