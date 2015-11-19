/*
 * Copyright (c) 2014 Jacob D. Parr
 *
 * This software may not be used without permission.
 */

package org.tiogasolutions.push.engine.core.resources.manage.client;

import org.tiogasolutions.push.kernel.clients.DomainProfileEntity;
import org.tiogasolutions.push.kernel.execution.ExecutionContext;
import org.tiogasolutions.push.kernel.execution.ExecutionManager;
import org.tiogasolutions.push.kernel.plugins.Plugin;
import org.tiogasolutions.push.kernel.system.PluginManager;

import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

public class ManageDomainModel {

  private final String message;
  private final ExecutionContext executionContext;

  private final Set<PluginModel> plugins = new TreeSet<>();

  public ManageDomainModel(ExecutionManager executionManager, DomainProfileEntity domainProfile, PluginManager pluginManager, String message) throws IOException {
    this.message = message;
    this.executionContext = executionManager.context();

    for (Plugin plugin : pluginManager.getPlugins()) {
      plugins.add(new PluginModel(domainProfile, plugin));
    }
  }

  public DomainProfileEntity getDomain() {
    return executionContext.getDomain();
  }

  public String getMessage() {
    return message;
  }

  public Set<PluginModel> getPlugins() {
    return plugins;
  }

}
