package org.tiogasolutions.push.engine.core.system;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.tiogasolutions.couchace.core.api.response.EntityDocument;
import org.tiogasolutions.dev.common.DateUtils;
import org.tiogasolutions.push.kernel.accounts.DomainStore;
import org.tiogasolutions.push.kernel.clients.DomainProfileEntity;
import org.tiogasolutions.push.kernel.requests.PushRequest;
import org.tiogasolutions.push.kernel.requests.QueryResult;
import org.tiogasolutions.push.kernel.requests.PushRequestStore;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.lang.String.format;

public class CpJobs {

  public static final Log log = LogFactory.getLog(CpJobs.class);

  private static final AtomicBoolean runningCompact = new AtomicBoolean(false);
  private static final AtomicBoolean runningPruner = new AtomicBoolean(false);

  private final DomainStore domainStore;
  private final PushRequestStore pushRequestStore;

  @Autowired
  public CpJobs(DomainStore domainStore, PushRequestStore pushRequestStore) {
    this.domainStore = domainStore;
    this.pushRequestStore = pushRequestStore;
  }

  public void cleanAndCompactDatabase() {
//    try {
//      if (runningCompact.compareAndSet(false, true)) {
//        CouchDatabase database = appContext.getDatabaseConfig().getCouchServer().database(CpCouchServer.DATABASE_NAME);
//        CouchUtils.compactAndCleanAll(database, CpCouchServer.designNames);
//      }
//    } catch (Exception ex) {
//      ex.printStackTrace();
//    } finally {
//      runningCompact.set(false);
//    }
  }

  public void pruneEvents() {
    try {
      if (runningPruner.compareAndSet(false, true)) {
        LocalDateTime now = DateUtils.currentLocalDateTime();

        List<DomainProfileEntity> domains = domainStore.getAll();
        for (DomainProfileEntity domain : domains) {
          pruneEvents(now, domain);
        }
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    } finally {
      runningPruner.set(false);
    }
  }

  private void pruneEvents(LocalDateTime now, DomainProfileEntity domain) {
    if (domain.getRetentionDays() <= 0) {
      return;
    }

    int count = 0;
    QueryResult<PushRequest> queryResult = pushRequestStore.getByClient(domain, 100);

    do {
      List<EntityDocument<PushRequest>> list = queryResult.getDocumentList();
      for (EntityDocument<PushRequest> document : list) {
        pruneEvents(now, domain, document);
      }

      count += queryResult.getSize();
      log.info(format("Deleted %s records\n", count));

    } while (queryResult.nextPage());
  }

  private void pruneEvents(LocalDateTime now, DomainProfileEntity domain, EntityDocument<PushRequest> document) {
    int days = domain.getRetentionDays();
    PushRequest pushRequest = document.getEntity();
    LocalDateTime later = pushRequest.getCreatedAt().plusWeeks(days);
    if (now.isAfter(later)) {
      pushRequestStore.deleteByDocumentId(
        document.getDocumentId(),
        document.getDocumentRevision()
      );
    }
  }
}
