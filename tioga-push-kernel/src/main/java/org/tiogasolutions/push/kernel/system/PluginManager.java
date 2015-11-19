package org.tiogasolutions.push.kernel.system;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tiogasolutions.push.kernel.execution.ExecutionManager;
import org.tiogasolutions.push.kernel.plugins.Plugin;
import org.tiogasolutions.push.pub.common.PushType;

import java.util.*;

@Component
public class PluginManager {

  private final Map<PushType,Plugin> map = new HashMap<>();

  @Autowired
  public PluginManager(ExecutionManager executionManager) {
    this(executionManager, 4);
  }

  public PluginManager(ExecutionManager executionManager, int expectedCount) {

    ServiceLoader<Plugin> loader = ServiceLoader.load(Plugin.class);
    loader.reload();

    for (Plugin plugin : loader) {
      plugin.init(executionManager);

      PushType pushType = plugin.getPushType();

      if (map.containsKey(pushType)) {
        String msg = String.format("The push type \"%s\" has already been registered.", pushType);
        throw new IllegalArgumentException(msg);
      }
      map.put(pushType, plugin);
    }

    if (map.size() < expectedCount) {
      String msg = String.format("Expected at least %s plugins but only found %s %s", expectedCount, map.size(), map.keySet());
      throw new IllegalStateException(msg);
    }
  }

  public List<Plugin> getPlugins() {
    return new ArrayList<>(map.values());
  }

  public Plugin getPlugin(PushType pushType) {
    if (map.containsKey(pushType) == false) {
      String msg = String.format("The plugin for \"%s\" was not found.", pushType.getCode());
      throw new IllegalArgumentException(msg);
    }
    return map.get(pushType);
  }
}
