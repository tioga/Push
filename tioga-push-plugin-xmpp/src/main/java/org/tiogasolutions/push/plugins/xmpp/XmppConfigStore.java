package org.tiogasolutions.push.plugins.xmpp;

import org.tiogasolutions.push.kernel.clients.DomainProfileEntity;
import org.tiogasolutions.push.kernel.execution.ExecutionManager;
import org.tiogasolutions.push.kernel.system.DomainSpecificStore;

public class XmppConfigStore extends DomainSpecificStore<XmppConfig> {

  public static final String XMPP_CONFIG_DESIGN_NAME = "xmpp-config";

  public XmppConfigStore(ExecutionManager executionManager) {
    super(executionManager, XmppConfig.class);
  }

  @Override
  public String getDesignName() {
    return XMPP_CONFIG_DESIGN_NAME;
  }

  public static String toDocumentId(DomainProfileEntity domain) {
    return String.format("%s:xmpp-config", domain.getDomainId());
  }
}
