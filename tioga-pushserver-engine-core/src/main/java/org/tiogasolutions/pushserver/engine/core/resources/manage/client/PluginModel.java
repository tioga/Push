package org.tiogasolutions.pushserver.engine.core.resources.manage.client;

import org.tiogasolutions.pushserver.common.accounts.Account;
import org.tiogasolutions.pushserver.common.clients.Domain;
import org.tiogasolutions.pushserver.pub.common.PushType;
import org.tiogasolutions.pushserver.common.plugins.Plugin;
import org.tiogasolutions.pushserver.common.plugins.PluginContext;

import java.io.IOException;

public class PluginModel implements Comparable<PluginModel>{

  private final String htmlContent;
  private final PushType pushType;

  public PluginModel(PluginContext context, Plugin plugin, Account account, Domain domain) throws IOException {
    this.pushType = plugin.getPushType();
    this.htmlContent = plugin.getAdminUi(context, domain);
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
