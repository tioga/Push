package org.tiogasolutions.push.plugins.ses;

import org.tiogasolutions.push.common.clients.Domain;
import org.tiogasolutions.push.common.system.CpCouchServer;
import org.tiogasolutions.lib.couchace.DefaultCouchStore;

public class SesEmailConfigStore extends DefaultCouchStore<SesEmailConfig> {

  public static final String SES_EMAIL_CONFIG_DESIGN_NAME = "ses-email-config";


  public SesEmailConfigStore(CpCouchServer couchServer) {
    super(couchServer, SesEmailConfig.class);
  }

  @Override
  public String getDatabaseName() {
    return CpCouchServer.DATABASE_NAME;
  }

  @Override
  public String getDesignName() {
    return SES_EMAIL_CONFIG_DESIGN_NAME;
  }

  public static String toDocumentId(Domain domain) {
    return String.format("%s:ses-email-config", domain.getDomainId());
  }
}
