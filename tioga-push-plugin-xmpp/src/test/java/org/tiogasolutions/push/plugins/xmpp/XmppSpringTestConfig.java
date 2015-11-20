package org.tiogasolutions.push.plugins.xmpp;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.tiogasolutions.dev.jackson.TiogaJacksonTranslator;
import org.tiogasolutions.push.jackson.CpObjectMapper;
import org.tiogasolutions.push.kernel.config.CouchServersConfig;
import org.tiogasolutions.push.kernel.execution.ExecutionManager;
import org.tiogasolutions.push.kernel.system.PluginManager;
import org.tiogasolutions.push.kernel.system.SessionStore;

import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import static java.util.Collections.*;

@Profile("test")
@Configuration
public class XmppSpringTestConfig {

  @Bean
  public ExecutionManager executionManager() {
    return new ExecutionManager();
  }

  @Bean
  public PluginManager pluginManager(ExecutionManager executionManager) {
    return new PluginManager(singletonList(new XmppPlugin(executionManager)));
  }
}
