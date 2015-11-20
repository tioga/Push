package org.tiogasolutions.push.plugins.xmpp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tiogasolutions.apis.bitly.BitlyApis;
import org.tiogasolutions.dev.common.Formats;
import org.tiogasolutions.dev.common.IoUtils;
import org.tiogasolutions.push.jackson.CpObjectMapper;
import org.tiogasolutions.push.kernel.KernelUtils;
import org.tiogasolutions.push.kernel.clients.DomainProfileEntity;
import org.tiogasolutions.push.kernel.execution.ExecutionContext;
import org.tiogasolutions.push.kernel.execution.ExecutionManager;
import org.tiogasolutions.push.kernel.plugins.PluginSupport;
import org.tiogasolutions.push.kernel.requests.PushRequest;
import org.tiogasolutions.push.kernel.requests.PushRequestStore;
import org.tiogasolutions.push.pub.XmppPush;
import org.tiogasolutions.push.pub.common.Push;

import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import java.io.InputStream;

import static org.tiogasolutions.dev.common.StringUtils.*;

@Component
public class XmppPlugin extends PluginSupport {

  private final BitlyApis bitlyApis;

  @Autowired
  public XmppPlugin(ExecutionManager executionManager, CpObjectMapper objectMapper, PushRequestStore pushRequestStore, BitlyApis bitlyApis) {
    super(XmppPush.PUSH_TYPE, executionManager, objectMapper, pushRequestStore);
    this.bitlyApis = bitlyApis;
  }

  public XmppConfigStore getConfigStore(ExecutionManager executionManager) {
    return new XmppConfigStore(executionManager);
  }

  @Override
  public XmppConfig getConfig(DomainProfileEntity domainProfile) {
    String docId = XmppConfigStore.toDocumentId(domainProfile);
    return getConfigStore(executionManager).getByDocumentId(docId);
  }

  @Override
  public XmppDelegate newDelegate(DomainProfileEntity domainProfile, PushRequest pushRequest, Push push) {
    XmppConfig config = getConfig(domainProfile);
    return new XmppDelegate(executionManager.context(), objectMapper, pushRequestStore, bitlyApis, pushRequest, (XmppPush)push, config);
  }

  @Override
  public void deleteConfig(DomainProfileEntity domainProfile) {

    XmppConfig config = getConfig(domainProfile);

    if (config != null) {
      getConfigStore(executionManager).delete(config);
      executionManager.context().setLastMessage("XMPP configuration deleted.");
    } else {
      executionManager.context().setLastMessage("XMPP configuration doesn't exist.");
    }
  }

  @Override
  public void updateConfig(DomainProfileEntity domainProfile, MultivaluedMap<String, String> formParams) {
    UpdateXmppConfigAction action = new UpdateXmppConfigAction(domainProfile, formParams);

    XmppConfig xmppConfig = getConfig(domainProfile);
    if (xmppConfig == null) {
      xmppConfig = new XmppConfig();
    }

    xmppConfig.apply(action);
    getConfigStore(executionManager).update(xmppConfig);

    executionManager.context().setLastMessage("XMPP configuration updated.");
  }

  @Override
  public void test(DomainProfileEntity domainProfile) throws Exception {
    XmppConfig config = getConfig(domainProfile);

    if (config == null) {
      String msg = "The XMPP config has not been specified.";
      executionManager.context().setLastMessage(msg);
      return;
    }

    String recipient = config.getTestAddress();

    if (isBlank((recipient))) {
      String msg = "Test message cannot be sent with out specifying the test address.";
      executionManager.context().setLastMessage(msg);
      return;
    }

    String override = config.getRecipientOverride();
    if (isNotBlank(override)) {
      recipient = override;
    }

    String when = Formats.defaultStamp(new java.util.Date());
    XmppPush push = XmppPush.newPush(recipient,
      String.format("XMPP test message from Cosmic Push sent at %s.", when),
      null, "xmpp-test:true");

    PushRequest pushRequest = new PushRequest(Push.CURRENT_API_VERSION, domainProfile, push);
    pushRequestStore.create(pushRequest);

    if (new XmppDelegate(executionManager.context(), objectMapper, pushRequestStore, bitlyApis, pushRequest, push, config).execute(false)) {
      String msg = String.format("Test message sent from %s to %s:\n%s", config.getUsername(), recipient, push.getMessage());
      executionManager.context().setLastMessage(msg);
    }
  }

  @Override
  public String getAdminUi(DomainProfileEntity domainProfile) throws IOException {
    ExecutionContext context = executionManager.context();
    String contextRoot = KernelUtils.getContextRoot(context.getUriInfo());

    XmppConfig config = getConfig(domainProfile);

    InputStream stream = getClass().getResourceAsStream("/org/tiogasolutions/push/plugins/xmpp/admin.html");
    String content = IoUtils.toString(stream);

    content = content.replace("${legend-class}",              nullToString(config == null ? "no-config" : ""));
    content = content.replace("${push-type}",                 nullToString(getPushType().getCode()));
    content = content.replace("${plugin-name}",               nullToString(getPluginName()));
    content = content.replace("${domain-key}",                nullToString(domainProfile.getDomainKey()));
    content = content.replace("${context-root}",              contextRoot);

    content = content.replace("${config-user-name}",          nullToString(config == null ? null : config.getUsername()));
    content = content.replace("${config-password}",           nullToString(config == null ? null : config.getPassword()));

    content = content.replace("${config-host}",               nullToString(config == null ? null : config.getHost()));
    content = content.replace("${config-port}",               nullToString(config == null ? null : config.getPort()));
    content = content.replace("${config-service-name}",       nullToString(config == null ? null : config.getServiceName()));

    content = content.replace("${config-test-address}",       nullToString(config == null ? null : config.getTestAddress()));
    content = content.replace("${config-recipient-override}", nullToString(config == null ? null : config.getRecipientOverride()));

    if (content.contains("${")) {
      String msg = "The XMPP admin UI still contains un-parsed elements.";
      throw new IllegalStateException(msg);
    }

    return content;
  }
}
