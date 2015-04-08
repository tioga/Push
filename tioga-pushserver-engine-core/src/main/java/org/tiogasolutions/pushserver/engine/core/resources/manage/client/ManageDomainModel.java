/*
 * Copyright (c) 2014 Jacob D. Parr
 *
 * This software may not be used without permission.
 */

package org.tiogasolutions.pushserver.engine.core.resources.manage.client;

import org.tiogasolutions.pushserver.common.accounts.Account;
import org.tiogasolutions.pushserver.common.clients.Domain;
import org.tiogasolutions.pushserver.common.plugins.Plugin;
import org.tiogasolutions.pushserver.common.plugins.PluginContext;
import org.tiogasolutions.pushserver.common.system.PluginManager;
import java.io.IOException;
import java.util.*;

public class ManageDomainModel {

  private final Domain domain;
  private final String message;

  private final Set<PluginModel> plugins = new TreeSet<>();

  public ManageDomainModel(PluginContext pluginContext, Account account, Domain domain, String message) throws IOException {
    this.message = message;
    this.domain = domain;

    for (Plugin plugin : PluginManager.getPlugins()) {
      plugins.add(new PluginModel(pluginContext, plugin, account, domain));
    }
  }

  public Domain getDomain() {
    return domain;
  }

  public String getMessage() {
    return message;
  }

  public Set<PluginModel> getPlugins() {
    return plugins;
  }

}
