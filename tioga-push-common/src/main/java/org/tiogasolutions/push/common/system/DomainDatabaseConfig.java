package org.tiogasolutions.push.common.system;

import org.tiogasolutions.couchace.core.api.CouchDatabase;
import org.tiogasolutions.dev.common.id.TimeUuidIdGenerator;
import org.tiogasolutions.lib.couchace.support.CouchUtils;
import org.tiogasolutions.push.common.requests.PushRequestStore;

import java.util.Arrays;
import java.util.List;

public class DomainDatabaseConfig {

  private final CpCouchServer couchServer;
  private final String dbName;

  public DomainDatabaseConfig(CpCouchServer couchServer, String dbName) {
    this.couchServer = couchServer;
    this.dbName = dbName;
  }

  public CpCouchServer getCouchServer() {
    return couchServer;
  }

  public String getDbName() {
    return dbName;
  }

  public void createDatabase(CouchDatabase database) {
    CouchUtils.createDatabase(database, new TimeUuidIdGenerator());

    List<String> designNames = Arrays.asList(
      "entity",
      PushRequestStore.PUSH_REQUEST_DESIGN_NAME);

    CouchUtils.validateDesign(database, designNames, "/push-server-common/design-docs/", "-design.json");
  }
}
