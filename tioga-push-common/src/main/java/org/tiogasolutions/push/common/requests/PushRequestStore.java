/*
 * Copyright (c) 2014 Jacob D. Parr
 *
 * This software may not be used without permission.
 */
package org.tiogasolutions.push.common.requests;

import org.tiogasolutions.push.common.clients.Domain;
import org.tiogasolutions.push.common.system.DomainDatabaseConfig;
import org.tiogasolutions.push.common.system.PushDomainSpecificStore;
import org.tiogasolutions.push.pub.common.PushType;
import org.tiogasolutions.couchace.core.api.query.CouchViewQuery;

import java.util.List;

public class PushRequestStore extends PushDomainSpecificStore<PushRequest> {

  public static final String PUSH_REQUEST_DESIGN_NAME = "push-request";

  public PushRequestStore(DomainDatabaseConfig databaseConfig) {
    super(databaseConfig, PushRequest.class);
  }

  public PushRequest getByPushRequestId(String pushRequestId) {
    return super.getByDocumentId(pushRequestId);
  }

  public QueryResult<PushRequest> getByClient(Domain domain, int limit) {

    CouchViewQuery viewQuery = CouchViewQuery.builder(getDesignName(), "byClient")
        .limit(limit)
        .key(domain.getDomainId())
        .build();

    return new QueryResult<>(PushRequest.class, getDatabase(), viewQuery);
  }

  public List<PushRequest> getByClientAndType(Domain domain, PushType type) {
    return super.getEntities("byClientAndType", domain.getDomainId(), type.getCode());
  }

  public List<PushRequest> getByClientAndSession(Domain domain, String sessionId) {
    return super.getEntities("byClientAndSession", domain.getDomainId(), sessionId);
  }

  public List<PushRequest> getByClientAndDevice(Domain domain, String deviceId) {
    return super.getEntities("byClientAndDevice", domain.getDomainId(), deviceId);
  }

  @Override
  public String getDesignName() {
    return PUSH_REQUEST_DESIGN_NAME;
  }
}
