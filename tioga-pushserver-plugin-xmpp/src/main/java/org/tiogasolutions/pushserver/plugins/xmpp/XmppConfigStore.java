package org.tiogasolutions.pushserver.plugins.xmpp;

import org.tiogasolutions.pushserver.common.clients.Domain;
import org.tiogasolutions.pushserver.common.system.CpCouchServer;
import org.tiogasolutions.lib.couchace.DefaultCouchStore;

public class XmppConfigStore extends DefaultCouchStore<XmppConfig> {

  public static final String XMPP_CONFIG_DESIGN_NAME = "xmpp-config";

  public XmppConfigStore(CpCouchServer couchServer) {
    super(couchServer, XmppConfig.class);
  }

  @Override
  public String getDatabaseName() {
    return CpCouchServer.DATABASE_NAME;
  }

  @Override
  public String getDesignName() {
    return XMPP_CONFIG_DESIGN_NAME;
  }

  public static String toDocumentId(Domain domain) {
    return String.format("%s:xmpp-config", domain.getDomainId());
  }
}
