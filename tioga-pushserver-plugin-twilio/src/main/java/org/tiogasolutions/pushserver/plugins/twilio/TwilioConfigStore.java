/*
 * Copyright (c) 2014 Jacob D. Parr
 *
 * This software may not be used without permission.
 */

package org.tiogasolutions.pushserver.plugins.twilio;

import org.tiogasolutions.pushserver.common.clients.Domain;
import org.tiogasolutions.pushserver.common.system.CpCouchServer;
import org.tiogasolutions.lib.couchace.DefaultCouchStore;

public class TwilioConfigStore extends DefaultCouchStore<TwilioConfig> {

  public static final String TWILIO_CONFIG_DESIGN_NAME = "twilio-config";

  public TwilioConfigStore(CpCouchServer couchServer) {
    super(couchServer, TwilioConfig.class);
  }

  @Override
  public String getDatabaseName() {
    return CpCouchServer.DATABASE_NAME;
  }

  @Override
  public String getDesignName() {
    return TWILIO_CONFIG_DESIGN_NAME;
  }

  public static String toDocumentId(Domain domain) {
    return String.format("%s:twilio-config", domain.getDomainId());
  }
}
