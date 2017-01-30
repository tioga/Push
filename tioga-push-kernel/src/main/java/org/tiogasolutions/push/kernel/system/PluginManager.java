package org.tiogasolutions.push.kernel.system;

import org.tiogasolutions.push.kernel.plugins.Plugin;
import org.tiogasolutions.push.pub.common.PushType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PluginManager {

    private final Map<PushType, Plugin> map = new HashMap<>();

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

    public <T extends Plugin> T getPlugin(Class<T> type) {
        for (Plugin plugin : map.values()) {
            if (type == plugin.getClass()) {
                // noinspection unchecked
                return (T) plugin;
            }
        }
        return null;
    }

    public Plugin getPlugin(PushType pushType) {
        if (map.containsKey(pushType) == false) {
            String msg = String.format("The plugin for \"%s\" was not found.", pushType.getCode());
            throw new IllegalArgumentException(msg);
        }
        return map.get(pushType);
    }
}
