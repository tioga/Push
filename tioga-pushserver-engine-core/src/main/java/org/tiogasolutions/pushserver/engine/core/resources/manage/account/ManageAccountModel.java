/*
 * Copyright (c) 2014 Jacob D. Parr
 *
 * This software may not be used without permission.
 */
package org.tiogasolutions.pushserver.engine.core.resources.manage.account;

import org.tiogasolutions.pushserver.common.accounts.Account;
import org.tiogasolutions.pushserver.common.clients.Domain;
import org.tiogasolutions.pushserver.common.plugins.Plugin;
import org.tiogasolutions.pushserver.common.plugins.PluginConfig;
import org.tiogasolutions.pushserver.common.plugins.PluginContext;
import org.tiogasolutions.pushserver.common.system.PluginManager;
import org.tiogasolutions.pushserver.pub.common.PushType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class ManageAccountModel {

  private final Account account;
  private final List<DomainModel> domains = new ArrayList<>();

  public ManageAccountModel(PluginContext pluginContext, Account account, List<Domain> domains) throws IOException {
    this.account = account;

    for (Domain domain : domains) {
      DomainModel domainModel = new DomainModel(domain.getDomainKey());
      this.domains.add(domainModel);

      for (Plugin plugin : PluginManager.getPlugins()) {
        PluginConfig config = plugin.getConfig(pluginContext.getCouchServer(), domain);
        if (config != null) {
          domainModel.enabledTypes.add(plugin.getPushType());
        } else {
          domainModel.disabledTypes.add(plugin.getPushType());
        }
      }
    }
  }

  public Account getAccount() {
    return account;
  }

  public List<DomainModel> getDomains() {
    return domains;
  }

  public static class DomainModel {
    private final String domainKey;
    private final Set<PushType> enabledTypes = new TreeSet<>();
    private final Set<PushType> disabledTypes = new TreeSet<>();
    public DomainModel(String domainKey) {
      this.domainKey = domainKey;
    }
    public String getDomainKey() { return domainKey; }
    public Set<PushType> getEnabledTypes() { return enabledTypes; }
    public Set<PushType> getDisabledTypes() { return disabledTypes; }
  }
}
