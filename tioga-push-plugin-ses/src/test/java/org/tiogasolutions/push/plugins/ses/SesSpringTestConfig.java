package org.tiogasolutions.push.plugins.ses;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.tiogasolutions.push.jackson.PushObjectMapper;
import org.tiogasolutions.push.kernel.config.CouchServersConfig;
import org.tiogasolutions.push.kernel.execution.ExecutionManager;
import org.tiogasolutions.push.kernel.requests.PushRequestStore;
import org.tiogasolutions.push.kernel.system.PluginManager;

import static java.util.Collections.singletonList;

@Profile("test")
@Configuration
public class SesSpringTestConfig {

  @Bean
  public CouchServersConfig couchServersConfig() {
    CouchServersConfig config = new CouchServersConfig();

    config.setMasterUrl("http://localhost:5984");
    config.setMasterUsername("test-user");
    config.setMasterPassword("test-user");
    config.setMasterDatabaseName("test-push");

    config.setDomainUrl("http://localhost:5984");
    config.setDomainUsername("test-user");
    config.setDomainPassword("test-user");
    config.setDomainDatabasePrefix("test-push-");

    return config;
  }

  @Bean
  public PluginManager pluginManager(ExecutionManager executionManager, PushObjectMapper objectMapper, PushRequestStore pushRequestStore) {
    return new PluginManager(singletonList(new SesEmailPlugin(executionManager, objectMapper, pushRequestStore)));
  }
}
