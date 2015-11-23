package org.tiogasolutions.push.engine;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.tiogasolutions.apis.bitly.BitlyApis;
import org.tiogasolutions.push.jackson.PushObjectMapper;
import org.tiogasolutions.push.kernel.execution.ExecutionManager;
import org.tiogasolutions.push.kernel.requests.PushRequestStore;
import org.tiogasolutions.push.kernel.system.PluginManager;
import org.tiogasolutions.push.plugins.ses.SesEmailPlugin;
import org.tiogasolutions.push.plugins.smtp.SmtpEmailPlugin;
import org.tiogasolutions.push.plugins.twilio.TwilioPlugin;
import org.tiogasolutions.push.plugins.xmpp.XmppPlugin;

import java.util.Arrays;

@Profile("test")
@Configuration
public class EngineSpringTestConfig {

  @Bean
  public ExecutionManager executionManager() {
    return new ExecutionManager();
  }

  /** @noinspection SpringJavaAutowiringInspection*/
  @Bean
  public PluginManager pluginManager(ExecutionManager executionManager, PushObjectMapper objectMapper, PushRequestStore pushRequestStore, BitlyApis bitlyApis) {
    return new PluginManager(Arrays.asList(
      new XmppPlugin(executionManager, objectMapper, pushRequestStore, bitlyApis),
      new SesEmailPlugin(executionManager, objectMapper, pushRequestStore, bitlyApis),
      new SmtpEmailPlugin(executionManager, objectMapper, pushRequestStore, bitlyApis),
      new TwilioPlugin(executionManager, objectMapper, pushRequestStore, bitlyApis)
    ));
  }
}
