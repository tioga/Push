package org.tiogasolutions.push.engine.core.resources.manage.client;

import org.tiogasolutions.push.kernel.clients.DomainProfileEntity;
import org.tiogasolutions.push.kernel.plugins.Plugin;
import org.tiogasolutions.push.pub.common.PushType;

import java.io.IOException;

public class PluginModel implements Comparable<PluginModel>{

  private final String htmlContent;
  private final PushType pushType;

  public PluginModel(DomainProfileEntity domainProfile, Plugin plugin) throws IOException {
    this.pushType = plugin.getPushType();
    this.htmlContent = plugin.getAdminUi(domainProfile);
  }

  public PushType getPushType() {
    return pushType;
  }

  public String getHtmlContent() {
    return htmlContent;
  }

  @Override
  public int compareTo(PluginModel that) {
    return this.pushType.compareTo(that.pushType);
  }
}
