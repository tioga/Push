package org.tiogasolutions.push.common.accounts;

import org.tiogasolutions.push.common.system.CpCouchServer;
import org.tiogasolutions.push.common.clients.Domain;
import org.tiogasolutions.lib.couchace.DefaultCouchStore;

import java.util.List;

public class DomainStore extends DefaultCouchStore<Domain> {

  public static final String DOMAIN_DESIGN_NAME = "domain";

  public DomainStore(CpCouchServer couchServer) {
    super(couchServer, Domain.class);
  }

  @Override
  public String getDesignName() {
    return DOMAIN_DESIGN_NAME;
  }

  @Override
  public String getDatabaseName() {
    return CpCouchServer.DATABASE_NAME;
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
}
