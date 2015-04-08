package org.tiogasolutions.pushserver.plugins.smtp;

import org.tiogasolutions.pushserver.common.clients.Domain;
import org.tiogasolutions.pushserver.common.system.CpCouchServer;
import org.tiogasolutions.lib.couchace.DefaultCouchStore;

public class SmtpEmailConfigStore extends DefaultCouchStore<SmtpEmailConfig> {

  public static final String SMTP_EMAIL_CONFIG_DESIGN_NAME = "smtp-email-config";


  public SmtpEmailConfigStore(CpCouchServer couchServer) {
    super(couchServer, SmtpEmailConfig.class);
  }

  @Override
  public String getDatabaseName() {
    return CpCouchServer.DATABASE_NAME;
  }

  @Override
  public String getDesignName() {
    return SMTP_EMAIL_CONFIG_DESIGN_NAME;
  }

  public static String toDocumentId(Domain domain) {
    return String.format("%s:smtp-email-config", domain.getDomainId());
  }
}
