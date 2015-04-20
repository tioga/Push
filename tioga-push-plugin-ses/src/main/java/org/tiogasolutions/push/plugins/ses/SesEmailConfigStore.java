package org.tiogasolutions.push.plugins.ses;

import org.tiogasolutions.push.common.clients.Domain;
import org.tiogasolutions.push.common.system.DomainDatabaseConfig;
import org.tiogasolutions.push.common.system.PushDomainSpecificStore;

public class SesEmailConfigStore extends PushDomainSpecificStore<SesEmailConfig> {

  public static final String SES_EMAIL_CONFIG_DESIGN_NAME = "ses-email-config";


  public SesEmailConfigStore(DomainDatabaseConfig databaseConfig) {
    super(databaseConfig, SesEmailConfig.class);
  }

  @Override
  public String getDesignName() {
    return SES_EMAIL_CONFIG_DESIGN_NAME;
  }

  public static String toDocumentId(Domain domain) {
    return String.format("%s:ses-email-config", domain.getDomainId());
  }
}
