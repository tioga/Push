package org.tiogasolutions.push.kernel.system;

import org.tiogasolutions.push.kernel.plugins.Plugin;
import org.tiogasolutions.push.pub.common.PushType;

import java.util.*;

public class PluginManager {

  private final Map<PushType,Plugin> map = new HashMap<>();

  public PluginManager(List<Plugin> plugins) {

    for (Plugin plugin : plugins) {
      PushType pushType = plugin.getPushType();

      if (map.containsKey(pushType)) {
        String msg = String.format("The push type \"%s\" has already been registered.", pushType);
        throw new IllegalArgumentException(msg);
      }
      map.put(pushType, plugin);
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
