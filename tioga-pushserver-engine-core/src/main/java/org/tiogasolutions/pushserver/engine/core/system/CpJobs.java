package org.tiogasolutions.pushserver.engine.core.system;

import org.tiogasolutions.pushserver.common.accounts.Account;
import org.tiogasolutions.pushserver.common.clients.Domain;
import org.tiogasolutions.pushserver.common.requests.PushRequest;
import org.tiogasolutions.pushserver.common.requests.QueryResult;
import org.tiogasolutions.pushserver.common.system.AppContext;
import org.tiogasolutions.pushserver.common.system.CpCouchServer;
import org.tiogasolutions.couchace.core.api.CouchDatabase;
import org.tiogasolutions.couchace.core.api.response.EntityDocument;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tiogasolutions.dev.common.DateUtils;
import org.tiogasolutions.lib.couchace.support.CouchUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.lang.String.format;

public class CpJobs {

  public static final Log log = LogFactory.getLog(CpJobs.class);

  private static final AtomicBoolean runningCompact = new AtomicBoolean(false);
  private static final AtomicBoolean runningPruner = new AtomicBoolean(false);

  private final AppContext appContext;

  public CpJobs(AppContext appContext) {
    this.appContext = appContext;
  }

  public void cleanAndCompactDatabase() {
    try {
      if (runningCompact.compareAndSet(false, true)) {
        CouchDatabase database = appContext.getCouchServer().database(CpCouchServer.DATABASE_NAME);
        CouchUtils.compactAndCleanAll(database, CpCouchServer.designNames);
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    } finally {
      runningCompact.set(false);
    }
  }

  public void pruneEvents() {
    try {
      if (runningPruner.compareAndSet(false, true)) {
        LocalDateTime now = DateUtils.currentLocalDateTime();
        List<Account> accounts = appContext.getAccountStore().getAll();

        List<Domain> domains = appContext.getDomainStore().getAll();
        for (Domain domain : domains) {
          pruneEvents(now, domain);
        }
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    } finally {
      runningPruner.set(false);
    }
  }

  private void pruneEvents(LocalDateTime now, Domain domain) {
    if (domain.getRetentionDays() <= 0) {
      return;
    }

    int count = 0;
    QueryResult<PushRequest> queryResult = appContext.getPushRequestStore().getByClient(domain, 100);

    do {
      List<EntityDocument<PushRequest>> list = queryResult.getDocumentList();
      for (EntityDocument<PushRequest> document : list) {
        pruneEvents(now, domain, document);
      }

      count += queryResult.getSize();
      log.info(format("Deleted %s records\n", count));

    } while (queryResult.nextPage());
  }

  private void pruneEvents(LocalDateTime now, Domain domain, EntityDocument<PushRequest> document) {
    int days = domain.getRetentionDays();
    PushRequest pushRequest = document.getEntity();
    LocalDateTime later = pushRequest.getCreatedAt().plusWeeks(days);
    if (now.isAfter(later)) {
      appContext.getPushRequestStore().deleteByDocumentId(
        document.getDocumentId(),
        document.getDocumentRevision()
      );
    }
  }
}
