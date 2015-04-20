package org.tiogasolutions.push.plugins.notifier;

import org.tiogasolutions.push.common.clients.Domain;
import org.tiogasolutions.push.common.system.DomainDatabaseConfig;
import org.tiogasolutions.push.common.system.PushDomainSpecificStore;

public class LqNotificationsConfigStore extends PushDomainSpecificStore<LqNotificationsConfig> {

  public static final String NOTIFIER_CONFIG_DESIGN_NAME = "notifier-config";

  public LqNotificationsConfigStore(DomainDatabaseConfig databaseConfig) {
    super(databaseConfig, LqNotificationsConfig.class);
  }

  @Override
  public String getDesignName() {
    return NOTIFIER_CONFIG_DESIGN_NAME;
  }

  public static String toDocumentId(Domain domain) {
    return String.format("%s:notifier-config", domain.getDomainId());
  }
}
