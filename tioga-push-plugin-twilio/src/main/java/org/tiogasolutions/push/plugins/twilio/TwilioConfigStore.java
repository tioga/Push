/*
 * Copyright (c) 2014 Jacob D. Parr
 *
 * This software may not be used without permission.
 */

package org.tiogasolutions.push.plugins.twilio;

import org.tiogasolutions.push.kernel.clients.DomainProfileEntity;
import org.tiogasolutions.push.kernel.execution.ExecutionManager;
import org.tiogasolutions.push.kernel.system.DomainSpecificStore;

public class TwilioConfigStore extends DomainSpecificStore<TwilioConfig> {

  public static final String TWILIO_CONFIG_DESIGN_NAME = "twilio-config";

  public TwilioConfigStore(ExecutionManager executionManager) {
    super(executionManager, TwilioConfig.class);
  }

  @Override
  public String getDesignName() {
    return TWILIO_CONFIG_DESIGN_NAME;
  }

  public static String toDocumentId(DomainProfileEntity domain) {
    return String.format("%s:twilio-config", domain.getDomainId());
  }
}
