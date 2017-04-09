package org.tiogasolutions.push.kernel.accounts;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tiogasolutions.couchace.core.api.CouchDatabase;
import org.tiogasolutions.lib.couchace.DefaultCouchStore;
import org.tiogasolutions.push.kernel.clients.DomainProfileEntity;
import org.tiogasolutions.push.kernel.config.CouchServersConfig;
import org.tiogasolutions.push.kernel.system.PushCouchServer;

import java.util.List;

@Component
public class DomainStore extends DefaultCouchStore<DomainProfileEntity> {

    public static final String DOMAIN_DESIGN_NAME = "domainProfile";

    private final String databaseName;

    @Autowired
    public DomainStore(PushCouchServer couchServer, CouchServersConfig config) {
        super(couchServer, DomainProfileEntity.class);
        this.databaseName = config.getMasterDatabaseName();
    }

    @Override
    public String getDatabaseName() {
        return databaseName;
    }

    @Override
    public String getDesignName() {
        return DOMAIN_DESIGN_NAME;
    }

    public List<DomainProfileEntity> getDomains(Account account) {
        if (account == null) return null;
        return super.getEntities("byAccountId", account.getAccountId());
    }

    public DomainProfileEntity getByDomainName(String domainKey) {
        if (domainKey == null) return null;
        List<DomainProfileEntity> response = super.getEntities("byDomainKey", domainKey);
        return (response.isEmpty()) ? null : response.get(0);
    }

    public List<DomainProfileEntity> getAll() {
        return super.getEntities("byDomainKey");
    }

    @Override
    public void createDatabase(CouchDatabase database) {
        PushCouchServer.createMainDatabase(database);
    }
}
