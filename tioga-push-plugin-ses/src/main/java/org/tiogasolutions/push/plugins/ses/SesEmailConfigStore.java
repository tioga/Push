package org.tiogasolutions.push.plugins.ses;

import org.tiogasolutions.push.kernel.clients.DomainProfileEntity;
import org.tiogasolutions.push.kernel.execution.ExecutionManager;
import org.tiogasolutions.push.kernel.system.DomainSpecificStore;

public class SesEmailConfigStore extends DomainSpecificStore<SesEmailConfig> {

  public static final String SES_EMAIL_CONFIG_DESIGN_NAME = "ses-email-config";


  public SesEmailConfigStore(ExecutionManager executionManager) {
    super(executionManager, SesEmailConfig.class);
  }

  @Override
  public String getDesignName() {
    return SES_EMAIL_CONFIG_DESIGN_NAME;
  }

  public static String toDocumentId(DomainProfileEntity domain) {
    return String.format("%s:ses-email-config", domain.getDomainId());
  }
}
