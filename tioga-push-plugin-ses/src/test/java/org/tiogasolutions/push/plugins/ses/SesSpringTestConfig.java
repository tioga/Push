package org.tiogasolutions.push.plugins.ses;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.tiogasolutions.push.kernel.execution.ExecutionManager;
import org.tiogasolutions.push.kernel.system.PluginManager;

import java.util.Collections;

import static java.util.Collections.*;

@Profile("test")
@Configuration
public class SesSpringTestConfig {

  @Bean
  public ExecutionManager executionManager() {
    return new ExecutionManager();
  }

  @Bean
  public PluginManager pluginManager(ExecutionManager executionManager) {
    return new PluginManager(singletonList(new SesEmailPlugin(executionManager)));
  }
}
