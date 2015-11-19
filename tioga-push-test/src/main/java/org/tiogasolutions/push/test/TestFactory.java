/*
 * Copyright (c) 2014 Jacob D. Parr
 *
 * This software may not be used without permission.
 */

package org.tiogasolutions.push.test;

import org.tiogasolutions.couchace.core.api.CouchDatabase;
import org.tiogasolutions.couchace.core.api.CouchServer;
import org.tiogasolutions.couchace.core.api.request.CouchFeature;
import org.tiogasolutions.couchace.core.api.request.CouchFeatureSet;
import org.tiogasolutions.dev.common.DateUtils;
import org.tiogasolutions.lib.couchace.DefaultCouchServer;
import org.tiogasolutions.push.jackson.CpObjectMapper;
import org.tiogasolutions.push.kernel.accounts.Account;
import org.tiogasolutions.push.kernel.accounts.AccountStore;
import org.tiogasolutions.push.kernel.accounts.DomainStore;
import org.tiogasolutions.push.kernel.accounts.actions.CreateAccountAction;
import org.tiogasolutions.push.kernel.actions.CreateDomainAction;
import org.tiogasolutions.push.kernel.clients.DomainProfileEntity;
import org.tiogasolutions.push.kernel.config.CouchServersConfig;
import org.tiogasolutions.push.kernel.execution.ExecutionManager;
import org.tiogasolutions.push.kernel.requests.PushRequest;
import org.tiogasolutions.push.kernel.requests.PushRequestStore;
import org.tiogasolutions.push.kernel.system.PluginManager;
import org.tiogasolutions.push.kernel.system.PushCouchServer;
import org.tiogasolutions.push.kernel.system.Session;
import org.tiogasolutions.push.pub.SmtpEmailPush;
import org.tiogasolutions.push.pub.common.Push;
import org.tiogasolutions.push.pub.common.UserAgent;

import java.time.ZoneId;
import java.util.*;

public class TestFactory {

  public static final ZoneId westCoastTimeZone = DateUtils.PDT;

  private final CouchServersConfig couchServersConfig;
  private final PushCouchServer couchServer;
  private final CpObjectMapper objectMapper;
  private final AccountStore accountStore;
  private final DomainStore domainStore;
  private final PluginManager pluginManager;

  private final ExecutionManager executionManager;

  public TestFactory(int expectedPluginCount) {

    objectMapper = new CpObjectMapper();

    CouchServer server = new DefaultCouchServer();


    String sysDatabase = "test-push";
    String usrDatabase = "test-push-domain";

    for (String dbName : Arrays.asList(sysDatabase, usrDatabase)) {
      CouchDatabase database = server.database(dbName, CouchFeatureSet.builder()
        .add(CouchFeature.ALLOW_DB_DELETE, true)
        .build());

      if (database.exists()) {
        database.deleteDatabase();
      }
    }

    couchServer = new PushCouchServer();

    couchServersConfig = new CouchServersConfig();
    couchServersConfig.setMasterUrl("http://localhost:5984");
    couchServersConfig.setMasterUserName("test-user");
    couchServersConfig.setMasterPassword("test-user");
    couchServersConfig.setMasterDatabaseName("test-push");

    couchServersConfig.setDomainUrl("http://localhost:5984");
    couchServersConfig.setDomainUserName("app-user");
    couchServersConfig.setDomainPassword("app-user");
    couchServersConfig.setDomainDatabasePrefix("test-push-");

    accountStore = new AccountStore(couchServer, couchServersConfig);
    domainStore = new DomainStore(couchServer, couchServersConfig);

    executionManager = new MockExecutionManager(this);
    pluginManager = new PluginManager(executionManager, expectedPluginCount);
  }

  public ExecutionManager getExecutionManager() {
    return executionManager;
  }

  public PushCouchServer getCouchServer() {
    return couchServer;
  }

  public CouchServersConfig getCouchServersConfig() {
    return couchServersConfig;
  }

  public CpObjectMapper getObjectMapper() {
    return objectMapper;
  }

  public AccountStore getAccountStore() {
    return accountStore;
  }

  public PushRequestStore getPushRequestStore() {
    return new PushRequestStore(executionManager);
  }

  public DomainStore getDomainStore() {
    return domainStore;
  }

  public UserAgent createUserAgent() {
    return new UserAgent(
        "agent-type", "agent-name", "agent-version", "agent-language", "agent-lang-tag",
        "os-type", "os-name", "os=produceer", "osproducer-url", "os-version-name", "os-version-number",
        "linux-distro"
    );
  }

  public Account createAccount() {
    CreateAccountAction createAccount = new CreateAccountAction(
        TestFactory.westCoastTimeZone,
        "test@jacobparr.com",
        "Unit", "Test",
        "testing123", "testing123"
    );

    return new Account(createAccount);
  }

  public DomainProfileEntity createDomain(Account account) {
    CreateDomainAction createClient = new CreateDomainAction(account, "some-key", "some-password");
    DomainProfileEntity domain = account.add(createClient);
    domainStore.create(domain);
    return domain;
  }

  public List<PushRequest> createPushRequests_Emails(DomainProfileEntity domain) throws Exception {
    List<PushRequest> requests = new ArrayList<>();

    Push push = SmtpEmailPush.newPush("to@example.com", "from@example.com", "This is the subject", "<html><body><h1>Hello World</h1></body></html>", null);
    PushRequest pushRequest = new PushRequest(Push.CURRENT_API_VERSION, domain, push);
    getPushRequestStore().create(pushRequest);
    requests.add(pushRequest);

    return requests;
  }

  public List<PushRequest> createPushRequests(DomainProfileEntity domain) throws Exception {
    Set<PushRequest> requests = new TreeSet<>();

    requests.addAll(createPushRequests_Emails(domain));

    return new ArrayList<>(requests);
  }

  public Session createSession() {
    return new Session(0, "test@example.com");
  }

  public PluginManager getPluginManager() {
    return pluginManager;
  }
}
