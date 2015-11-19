package org.tiogasolutions.push.kernel.system;

import com.fasterxml.jackson.databind.Module;
import org.springframework.stereotype.Component;
import org.tiogasolutions.couchace.core.api.CouchDatabase;
import org.tiogasolutions.dev.common.id.TimeUuidIdGenerator;
import org.tiogasolutions.dev.jackson.TiogaJacksonModule;
import org.tiogasolutions.lib.couchace.DefaultCouchServer;
import org.tiogasolutions.lib.couchace.support.CouchUtils;
import org.tiogasolutions.push.kernel.accounts.AccountStore;
import org.tiogasolutions.push.kernel.accounts.DomainStore;
import org.tiogasolutions.push.jackson.CpJacksonModule;

import java.util.Arrays;
import java.util.List;

@Component
public class PushCouchServer extends DefaultCouchServer {

  public PushCouchServer() {
    super(new Module[]{
      new TiogaJacksonModule(),
      new CpJacksonModule()});
  }

  public static void createMainDatabase(CouchDatabase database) {
    CouchUtils.createDatabase(database, new TimeUuidIdGenerator(),
      "/push-server-common/json/account.json");

    List<String> designNames = Arrays.asList(
      "entity",
      DomainStore.DOMAIN_DESIGN_NAME,
      AccountStore.ACCOUNT_DESIGN_NAME);

    CouchUtils.validateDesign(database, designNames, "/push-server-common/design-docs/", "-design.json");
  }
}
