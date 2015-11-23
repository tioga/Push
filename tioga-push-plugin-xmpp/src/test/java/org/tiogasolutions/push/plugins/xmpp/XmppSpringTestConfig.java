package org.tiogasolutions.push.plugins.xmpp;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.tiogasolutions.apis.bitly.BitlyApis;
import org.tiogasolutions.push.jackson.PushObjectMapper;
import org.tiogasolutions.push.kernel.execution.ExecutionManager;
import org.tiogasolutions.push.kernel.requests.PushRequestStore;
import org.tiogasolutions.push.kernel.system.PluginManager;

import static java.util.Collections.singletonList;

@Profile("test")
@Configuration
public class XmppSpringTestConfig {

  @Bean
  public ExecutionManager executionManager() {
    return new ExecutionManager();
  }

  /** @noinspection SpringJavaAutowiringInspection*/
  @Bean
  public PluginManager pluginManager(ExecutionManager executionManager, PushObjectMapper objectMapper, PushRequestStore pushRequestStore, BitlyApis bitlyApis) {
    return new PluginManager(singletonList(new XmppPlugin(executionManager, objectMapper, pushRequestStore, bitlyApis)));
  }
}
