package org.tiogasolutions.push.plugins.smtp;

import org.tiogasolutions.push.common.clients.Domain;
import org.tiogasolutions.push.common.system.DomainDatabaseConfig;
import org.tiogasolutions.push.common.system.PushDomainSpecificStore;

public class SmtpEmailConfigStore extends PushDomainSpecificStore<SmtpEmailConfig> {

  public static final String SMTP_EMAIL_CONFIG_DESIGN_NAME = "smtp-email-config";


  public SmtpEmailConfigStore(DomainDatabaseConfig databaseConfig) {
    super(databaseConfig, SmtpEmailConfig.class);
  }

  @Override
  public String getDesignName() {
    return SMTP_EMAIL_CONFIG_DESIGN_NAME;
  }

  public static String toDocumentId(Domain domain) {
    return String.format("%s:smtp-email-config", domain.getDomainId());
  }
}
