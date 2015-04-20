package org.tiogasolutions.push.plugins.xmpp;

import org.tiogasolutions.push.common.clients.Domain;
import org.tiogasolutions.push.common.system.DomainDatabaseConfig;
import org.tiogasolutions.push.common.system.PushDomainSpecificStore;

public class XmppConfigStore extends PushDomainSpecificStore<XmppConfig> {

  public static final String XMPP_CONFIG_DESIGN_NAME = "xmpp-config";

  public XmppConfigStore(DomainDatabaseConfig databaseConfig) {
    super(databaseConfig, XmppConfig.class);
  }

  @Override
  public String getDesignName() {
    return XMPP_CONFIG_DESIGN_NAME;
  }

  public static String toDocumentId(Domain domain) {
    return String.format("%s:xmpp-config", domain.getDomainId());
  }
}
