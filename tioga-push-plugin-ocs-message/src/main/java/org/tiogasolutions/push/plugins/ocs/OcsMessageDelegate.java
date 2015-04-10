package com.cosmicpush.plugins.ocs;

import com.cosmicpush.common.AbstractDelegate;
import com.cosmicpush.common.accounts.Account;
import com.cosmicpush.common.clients.Domain;
import com.cosmicpush.common.plugins.PluginContext;
import com.cosmicpush.common.requests.PushRequest;
import com.cosmicpush.pub.common.RequestStatus;
import com.cosmicpush.pub.push.OcsPush;
import org.tiogasolutions.dev.common.StringUtils;
import org.tiogasolutions.dev.common.exceptions.ExceptionUtils;

public class OcsMessageDelegate extends AbstractDelegate {

  private final Account account;
  private final Domain domain;

  private final OcsPush push;
  private final OcsMessageConfig config;

  public OcsMessageDelegate(PluginContext context, Account account, Domain domain, PushRequest pushRequest, OcsPush push, OcsMessageConfig config) {
    super(context.getObjectMapper(), pushRequest, context.getPushRequestStore());
    this.config = ExceptionUtils.assertNotNull(config, "config");
    this.push = ExceptionUtils.assertNotNull(push, "push");
    this.account = ExceptionUtils.assertNotNull(account, "account");
    this.domain = ExceptionUtils.assertNotNull(domain, "domain");
  }

  @Override
  public synchronized RequestStatus processRequest() throws Exception {
    String reasonNotPermitted = account.getReasonNotPermitted(push);
    if (StringUtils.isNotBlank(reasonNotPermitted)) {
      return pushRequest.denyRequest(reasonNotPermitted);
    }

    return pushRequest.failed("Not implemented.");
  }
}
