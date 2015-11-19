package org.tiogasolutions.push.kernel.system;

import org.tiogasolutions.couchace.core.api.CouchDatabase;
import org.tiogasolutions.dev.common.exceptions.ApiException;
import org.tiogasolutions.dev.common.id.TimeUuidIdGenerator;
import org.tiogasolutions.lib.couchace.DefaultCouchStore;
import org.tiogasolutions.lib.couchace.support.CouchUtils;
import org.tiogasolutions.push.kernel.clients.DomainProfileEntity;
import org.tiogasolutions.push.kernel.config.CouchServersConfig;
import org.tiogasolutions.push.kernel.execution.ExecutionManager;
import org.tiogasolutions.push.kernel.requests.PushRequestStore;

import java.util.Arrays;
import java.util.List;

public abstract class DomainSpecificStore<T> extends DefaultCouchStore<T> {

  private final CouchServersConfig config;
  private final ExecutionManager executionManager;

  public DomainSpecificStore(ExecutionManager executionManager, Class<T> entityType) {
    super(executionManager.getCouchServer(), entityType);
    this.config = executionManager.getCouchServersConfig();
    this.executionManager = executionManager;
  }

  @Override
  public final String getDatabaseName() {
    DomainProfileEntity domain = executionManager.context().getDomain();
    if (domain == null) {
      throw ApiException.internalServerError("A domain does not exist within the execution context.");
    }
    return config.getDomainDatabasePrefix() + domain.getDomainKey();
  }

  @Override
  public final void createDatabase(CouchDatabase database) {
    CouchUtils.createDatabase(database, new TimeUuidIdGenerator());

    List<String> designNames = Arrays.asList(
      "entity",
      PushRequestStore.PUSH_REQUEST_DESIGN_NAME);

    CouchUtils.validateDesign(database, designNames, "/push-server-common/design-docs/", "-design.json");
  }
}
