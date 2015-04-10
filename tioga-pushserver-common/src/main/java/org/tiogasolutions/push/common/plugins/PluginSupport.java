package org.tiogasolutions.push.common.plugins;

import org.tiogasolutions.push.common.clients.Domain;
import org.tiogasolutions.push.pub.common.PushType;
import org.tiogasolutions.dev.common.IoUtils;

import java.io.IOException;
import java.io.InputStream;

public abstract class PluginSupport implements Plugin {

  private final String pluginName;
  private final PushType pushType;

  public PluginSupport(PushType pushType) {
    this.pushType = pushType;

    String name = getClass().getPackage().getName();
    this.pluginName = name.substring(name.lastIndexOf(".")+1);
  }

  public String getPluginName() {
    return pluginName;
  }

  @Override
  public final PushType getPushType() {
      return pushType;
  }

  @Override
  public final byte[] getIcon(PluginContext pluginContext, Domain domain) throws IOException {
    PluginConfig config = getConfig(pluginContext.getCouchServer(), domain);
    return (config == null) ? getDisabledIcon() : getEnabledIcon();
  }

  @Override
  public final byte[] getEnabledIcon() throws IOException {
      InputStream stream = getClass().getResourceAsStream("/com/cosmicpush/plugins/"+ pluginName +"/icon-enabled.png");
      return IoUtils.toBytes(stream);
  }

  @Override
  public final byte[] getDisabledIcon() throws IOException {
      InputStream stream = getClass().getResourceAsStream("/com/cosmicpush/plugins/"+ pluginName +"/icon-disabled.png");
      return IoUtils.toBytes(stream);
  }
}
