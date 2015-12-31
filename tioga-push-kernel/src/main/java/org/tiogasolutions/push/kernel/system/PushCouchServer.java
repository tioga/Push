package org.tiogasolutions.push.kernel.system;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tiogasolutions.couchace.core.api.CouchDatabase;
import org.tiogasolutions.couchace.core.api.CouchSetup;
import org.tiogasolutions.couchace.jackson.JacksonCouchJsonStrategy;
import org.tiogasolutions.couchace.jersey.JerseyCouchHttpClient;
import org.tiogasolutions.dev.common.id.TimeUuidIdGenerator;
import org.tiogasolutions.dev.jackson.TiogaJacksonModule;
import org.tiogasolutions.lib.couchace.DefaultCouchServer;
import org.tiogasolutions.lib.couchace.support.CouchUtils;
import org.tiogasolutions.push.jackson.PushJacksonModule;
import org.tiogasolutions.push.kernel.accounts.AccountStore;
import org.tiogasolutions.push.kernel.accounts.DomainStore;
import org.tiogasolutions.push.kernel.config.CouchServersConfig;

import java.util.Arrays;
import java.util.List;

@Component
public class PushCouchServer extends DefaultCouchServer {

  @Autowired
  public PushCouchServer(CouchServersConfig config) {
    super(createCouchSetup(config));
  }

  public static CouchSetup createCouchSetup(CouchServersConfig config) {
    CouchSetup setup = new CouchSetup(config.getMasterUrl());
    setup.setUserName(config.getMasterUsername());
    setup.setPassword(config.getMasterPassword());
    setup.setHttpClient(JerseyCouchHttpClient.class);
    setup.setJsonStrategy(new JacksonCouchJsonStrategy(
      new TiogaJacksonModule(),
      new PushJacksonModule()
    ));
    return setup;
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
