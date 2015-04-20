package org.tiogasolutions.push.common.system;

import org.tiogasolutions.couchace.core.api.CouchDatabase;
import org.tiogasolutions.lib.couchace.DefaultCouchStore;

public abstract class PushDomainSpecificStore<T> extends DefaultCouchStore<T> {

  private final DomainDatabaseConfig databaseConfig;

  public PushDomainSpecificStore(DomainDatabaseConfig databaseConfig, Class<T> entityType) {
    super(databaseConfig.getCouchServer(), entityType);
    this.databaseConfig = databaseConfig;
  }

  @Override
  public final String getDatabaseName() {
    return databaseConfig.getDbName();
  }

  public final DomainDatabaseConfig getDatabaseConfig() {
    return databaseConfig;
  }

  @Override
  public final void createDatabase(CouchDatabase database) {
    databaseConfig.createDatabase(database);
  }
}
