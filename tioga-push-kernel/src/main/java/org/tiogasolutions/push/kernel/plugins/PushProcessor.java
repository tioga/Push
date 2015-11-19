package org.tiogasolutions.push.kernel.plugins;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tiogasolutions.push.kernel.AbstractDelegate;
import org.tiogasolutions.push.kernel.clients.DomainProfileEntity;
import org.tiogasolutions.push.kernel.requests.PushRequest;
import org.tiogasolutions.push.kernel.requests.PushRequestStore;
import org.tiogasolutions.push.kernel.system.PluginManager;
import org.tiogasolutions.push.pub.common.PingPush;
import org.tiogasolutions.push.pub.common.Push;
import org.tiogasolutions.push.pub.common.PushResponse;
import org.tiogasolutions.push.pub.common.RequestStatus;

import java.time.LocalDateTime;
import java.util.Collections;

@Component
public class PushProcessor {

  private final PushRequestStore pushRequestStore;
  private final PluginManager pluginManager;

  @Autowired
  public PushProcessor(PluginManager pluginManager, PushRequestStore pushRequestStore) {
    this.pluginManager = pluginManager;
    this.pushRequestStore = pushRequestStore;
  }

  public PushResponse execute(int apiVersion, DomainProfileEntity domainProfile, Push push) {

    if (push instanceof PingPush) {
      return new PushResponse(
        domainProfile.getDomainId(),
        "0",
        LocalDateTime.now(),
        RequestStatus.processed,
        Collections.emptyList()
      );
    }

    // TODO - validate the remoteHost and remoteAddress specified in the push as really coming from them.
    PushRequest pushRequest = new PushRequest(apiVersion, domainProfile, push);

    pushRequestStore.create(pushRequest);

    Plugin plugin = pluginManager.getPlugin(push.getPushType());
    AbstractDelegate delegate = plugin.newDelegate(domainProfile, pushRequest, push);

    delegate.start();

    return new PushResponse(
        domainProfile.getDomainId(),
        pushRequest.getPushRequestId(),
        pushRequest.getCreatedAt(),
        pushRequest.getRequestStatus(),
        pushRequest.getNotes()
    );
  }
}
