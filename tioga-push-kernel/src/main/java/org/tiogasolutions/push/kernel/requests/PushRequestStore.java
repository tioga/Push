/*
 * Copyright (c) 2014 Jacob D. Parr
 *
 * This software may not be used without permission.
 */
package org.tiogasolutions.push.kernel.requests;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tiogasolutions.couchace.core.api.query.CouchViewQuery;
import org.tiogasolutions.push.kernel.clients.DomainProfileEntity;
import org.tiogasolutions.push.kernel.execution.ExecutionManager;
import org.tiogasolutions.push.kernel.system.DomainSpecificStore;
import org.tiogasolutions.push.pub.common.PushType;

import java.util.List;

@Component
public class PushRequestStore extends DomainSpecificStore<PushRequest> {

  public static final String PUSH_REQUEST_DESIGN_NAME = "push-request";

  @Autowired
  public PushRequestStore(ExecutionManager executionManager) {
    super(executionManager, PushRequest.class);
  }

  public PushRequest getByPushRequestId(String pushRequestId) {
    return super.getByDocumentId(pushRequestId);
  }

  public QueryResult<PushRequest> getByClient(DomainProfileEntity domain, int limit) {

    CouchViewQuery viewQuery = CouchViewQuery.builder(getDesignName(), "byClient")
        .limit(limit)
        .key(domain.getDomainId())
        .build();

    return new QueryResult<>(PushRequest.class, getDatabase(), viewQuery);
  }

  public List<PushRequest> getByClientAndType(DomainProfileEntity domain, PushType type) {
    return super.getEntities("byClientAndType", domain.getDomainId(), type.getCode());
  }

  public List<PushRequest> getByClientAndSession(DomainProfileEntity domain, String sessionId) {
    return super.getEntities("byClientAndSession", domain.getDomainId(), sessionId);
  }

  public List<PushRequest> getByClientAndDevice(DomainProfileEntity domain, String deviceId) {
    return super.getEntities("byClientAndDevice", domain.getDomainId(), deviceId);
  }

  @Override
  public String getDesignName() {
    return PUSH_REQUEST_DESIGN_NAME;
  }
}
