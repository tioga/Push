package org.tiogasolutions.push.kernel.plugins;

import org.tiogasolutions.dev.common.IoUtils;
import org.tiogasolutions.push.kernel.clients.DomainProfileEntity;
import org.tiogasolutions.push.kernel.execution.ExecutionManager;
import org.tiogasolutions.push.pub.common.PushType;

import java.io.IOException;
import java.io.InputStream;

public abstract class PluginSupport implements Plugin {

  private final String pluginName;
  private final PushType pushType;

  protected ExecutionManager executionManager;

  public PluginSupport(PushType pushType) {
    this.pushType = pushType;

    String name = getClass().getPackage().getName();
    this.pluginName = name.substring(name.lastIndexOf(".")+1);
  }

  public final void init(ExecutionManager executionManager) {
    this.executionManager = executionManager;
  }

  public final String getPluginName() {
    return pluginName;
  }

  @Override
  public final PushType getPushType() {
      return pushType;
  }

  public final byte[] getIcon(DomainProfileEntity domainProfile) throws IOException {
    PluginConfig config = getConfig(domainProfile);
    return (config == null) ? getDisabledIcon() : getEnabledIcon();
  }

  @Override
  public final byte[] getEnabledIcon() throws IOException {
      InputStream stream = getClass().getResourceAsStream("/org/tiogasolutions/push/plugins/"+ pluginName +"/icon-enabled.png");
      return IoUtils.toBytes(stream);
  }

  @Override
  public final byte[] getDisabledIcon() throws IOException {
      InputStream stream = getClass().getResourceAsStream("/org/tiogasolutions/push/plugins/"+ pluginName +"/icon-disabled.png");
      return IoUtils.toBytes(stream);
  }
}
