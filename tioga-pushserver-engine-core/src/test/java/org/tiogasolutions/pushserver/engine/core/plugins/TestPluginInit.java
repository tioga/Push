package org.tiogasolutions.pushserver.engine.core.plugins;

import org.tiogasolutions.pushserver.common.plugins.Plugin;
import java.util.ServiceLoader;
import org.testng.annotations.Test;

@Test
public class TestPluginInit {

  public void testEmailPlugin() throws Exception {
    ServiceLoader<Plugin> pluginLoader = ServiceLoader.load(Plugin.class);

    for (Plugin plugin : pluginLoader) {
      System.out.printf("Plugin: %s%n", plugin.getClass().getName());
    }
  }
}
