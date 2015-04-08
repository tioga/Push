package org.tiogasolutions.pushserver.common.system;

import org.tiogasolutions.pushserver.common.clients.Domain;
import org.tiogasolutions.pushserver.common.plugins.Plugin;
import org.tiogasolutions.pushserver.common.plugins.PluginConfig;
import org.tiogasolutions.pushserver.common.plugins.PluginContext;
import org.tiogasolutions.pushserver.pub.common.PushType;

import java.util.*;

public class PluginManager {

  private static final PluginManager SINGLETON = new PluginManager();

  private final Map<PushType,Plugin> map = new HashMap<>();

  private PluginManager() {

    ServiceLoader<Plugin> loader = ServiceLoader.load(Plugin.class);
    loader.reload();

    for (Plugin plugin : loader) {
      PushType pushType = plugin.getPushType();
      if (map.containsKey(pushType)) {
        String msg = String.format("The push type \"%s\" has already been registered.", pushType);
        throw new IllegalArgumentException(msg);
      }
      map.put(pushType, plugin);
    }

    int expectedCount = 4;
    if (map.size() < expectedCount) {
      String msg = String.format("Expected at least %s plugins but only found %s %s", expectedCount, map.size(), map.keySet());
      throw new IllegalStateException(msg);
    }
  }

  public static List<Plugin> getPlugins() {
    return new ArrayList<>(SINGLETON.map.values());
  }

  public static Plugin getPlugin(PushType pushType) {
    if (SINGLETON.map.containsKey(pushType) == false) {
      String msg = String.format("The plugin for \"%s\" was not found.", pushType.getCode());
      throw new IllegalArgumentException(msg);
    }
    return SINGLETON.map.get(pushType);
  }

  public static <T extends Plugin> T getPlugin(Class<T> type) {
    for (Plugin plugin : SINGLETON.map.values()) {
      if (type.equals(plugin.getClass())) {
        return type.cast(plugin);
      }
    }
    String msg = String.format("The plugin for \"%s\" was not found.", type.getName());
    throw new IllegalArgumentException(msg);
  }

  public static PluginConfig getConfig(PluginContext context, Domain domain, PushType pushType) {
    Plugin plugin = getPlugin(pushType);
    return plugin.getConfig(context.getCouchServer(), domain);
  }
}
