package org.tiogasolutions.push.plugins.smtp;

import org.tiogasolutions.push.kernel.clients.DomainProfileEntity;
import org.tiogasolutions.push.kernel.execution.ExecutionManager;
import org.tiogasolutions.push.kernel.system.DomainSpecificStore;

public class SmtpEmailConfigStore extends DomainSpecificStore<SmtpEmailConfig> {

  public static final String SMTP_EMAIL_CONFIG_DESIGN_NAME = "smtp-email-config";


  public SmtpEmailConfigStore(ExecutionManager executionManager) {
    super(executionManager, SmtpEmailConfig.class);
  }

  @Override
  public String getDesignName() {
    return SMTP_EMAIL_CONFIG_DESIGN_NAME;
  }

  public static String toDocumentId(DomainProfileEntity domain) {
    return String.format("%s:smtp-email-config", domain.getDomainId());
  }
}
