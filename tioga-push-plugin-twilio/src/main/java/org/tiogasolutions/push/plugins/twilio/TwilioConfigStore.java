/*
 * Copyright (c) 2014 Jacob D. Parr
 *
 * This software may not be used without permission.
 */

package org.tiogasolutions.push.plugins.twilio;

import org.tiogasolutions.push.common.clients.Domain;
import org.tiogasolutions.push.common.system.DomainDatabaseConfig;
import org.tiogasolutions.push.common.system.PushDomainSpecificStore;

public class TwilioConfigStore extends PushDomainSpecificStore<TwilioConfig> {

  public static final String TWILIO_CONFIG_DESIGN_NAME = "twilio-config";

  public TwilioConfigStore(DomainDatabaseConfig databaseConfig) {
    super(databaseConfig, TwilioConfig.class);
  }

  @Override
  public String getDesignName() {
    return TWILIO_CONFIG_DESIGN_NAME;
  }

  public static String toDocumentId(Domain domain) {
    return String.format("%s:twilio-config", domain.getDomainId());
  }
}
