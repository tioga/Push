package org.tiogasolutions.push.engine;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.tiogasolutions.push.kernel.execution.ExecutionManager;
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

  @Bean
  public PluginManager pluginManager(ExecutionManager executionManager) {
    return new PluginManager(Arrays.asList(
      new XmppPlugin(executionManager),
      new SesEmailPlugin(executionManager),
      new SmtpEmailPlugin(executionManager),
      new TwilioPlugin(executionManager)
    ));
  }
}
