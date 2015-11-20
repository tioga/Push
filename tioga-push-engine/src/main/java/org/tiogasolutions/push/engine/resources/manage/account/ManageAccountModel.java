/*
 * Copyright (c) 2014 Jacob D. Parr
 *
 * This software may not be used without permission.
 */
package org.tiogasolutions.push.engine.resources.manage.account;

import org.tiogasolutions.push.kernel.accounts.Account;
import org.tiogasolutions.push.kernel.clients.DomainProfileEntity;
import org.tiogasolutions.push.kernel.execution.ExecutionManager;
import org.tiogasolutions.push.kernel.plugins.Plugin;
import org.tiogasolutions.push.kernel.plugins.PluginConfig;
import org.tiogasolutions.push.kernel.system.PluginManager;
import org.tiogasolutions.push.pub.common.PushType;

import java.io.IOException;
import java.util.*;

public class ManageAccountModel {

  private final Account account;
  private final List<DomainModel> domains = new ArrayList<>();

  public ManageAccountModel(ExecutionManager executionManager, PluginManager pluginManager, Account account, DomainProfileEntity...domainProfiles) throws IOException {
    this(executionManager, pluginManager, account, Arrays.asList(domainProfiles));
  }

  public ManageAccountModel(ExecutionManager executionManager, PluginManager pluginManager, Account account, List<DomainProfileEntity> domainProfiles) throws IOException {
    this.account = account;

    for (DomainProfileEntity domainProfile : domainProfiles) {
      DomainModel domainModel = new DomainModel(domainProfile.getDomainKey());
      this.domains.add(domainModel);

      for (Plugin plugin : pluginManager.getPlugins()) {
        DomainProfileEntity oldProfile = executionManager.context().getDomain();
        executionManager.context().setDomain(domainProfile);
        try {
          PluginConfig config = plugin.getConfig(domainProfile);
          if (config != null) {
            domainModel.enabledTypes.add(plugin.getPushType());
          } else {
            domainModel.disabledTypes.add(plugin.getPushType());
          }
        } finally {
          executionManager.context().setDomain(oldProfile);
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
