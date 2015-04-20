package org.tiogasolutions.push.common.accounts;

import org.tiogasolutions.couchace.core.api.CouchDatabase;
import org.tiogasolutions.dev.common.id.TimeUuidIdGenerator;
import org.tiogasolutions.lib.couchace.DefaultCouchStore;
import org.tiogasolutions.lib.couchace.support.CouchUtils;
import org.tiogasolutions.push.common.clients.Domain;
import org.tiogasolutions.push.common.requests.PushRequestStore;
import org.tiogasolutions.push.common.system.CpCouchServer;
import org.tiogasolutions.push.common.system.DomainDatabaseConfig;
import org.tiogasolutions.push.common.system.PushDomainSpecificStore;

import java.util.Arrays;
import java.util.List;

public class DomainStore extends DefaultCouchStore<Domain> {

  public static final String DOMAIN_DESIGN_NAME = "domain";

  private final String databaseName;

  public DomainStore(CpCouchServer couchServer, String databaseName) {
    super(couchServer, Domain.class);
    this.databaseName = databaseName;
  }

  @Override
  public String getDatabaseName() {
    return databaseName;
  }

  @Override
  public String getDesignName() {
    return DOMAIN_DESIGN_NAME;
  }

  public List<Domain> getDomains(Account account) {
    if (account == null) return null;
    return super.getEntities("byAccountId", account.getAccountId());
  }

  public Domain getByDomainKey(String domainKey) {
    if (domainKey == null) return null;
    List<Domain> response = super.getEntities("byDomainKey", domainKey);
    return (response.isEmpty()) ? null : response.get(0);
  }

  public List<Domain> getAll() {
    return super.getEntities("byDomainKey");
  }

  @Override
  public void createDatabase(CouchDatabase database) {
    CpCouchServer.createMainDatabase(database);
  }
}
