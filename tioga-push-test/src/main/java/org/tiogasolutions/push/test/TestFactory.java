/*
 * Copyright (c) 2014 Jacob D. Parr
 *
 * This software may not be used without permission.
 */

package org.tiogasolutions.push.test;

import org.tiogasolutions.push.common.accounts.Account;
import org.tiogasolutions.push.common.accounts.AccountStore;
import org.tiogasolutions.push.common.accounts.DomainStore;
import org.tiogasolutions.push.common.accounts.actions.CreateAccountAction;
import org.tiogasolutions.push.common.actions.CreateDomainAction;
import org.tiogasolutions.push.common.clients.Domain;
import org.tiogasolutions.push.common.plugins.PluginContext;
import org.tiogasolutions.push.common.plugins.PushProcessor;
import org.tiogasolutions.push.common.requests.PushRequest;
import org.tiogasolutions.push.common.requests.PushRequestStore;
import org.tiogasolutions.push.common.system.AppContext;
import org.tiogasolutions.push.common.system.CpCouchServer;
import org.tiogasolutions.push.common.system.Session;
import org.tiogasolutions.push.jackson.CpObjectMapper;
import org.tiogasolutions.push.pub.common.Push;
import org.tiogasolutions.push.pub.common.UserAgent;
import org.tiogasolutions.push.pub.LqNotificationPush;
import org.tiogasolutions.push.pub.SmtpEmailPush;
import org.tiogasolutions.couchace.core.api.CouchDatabase;
import org.tiogasolutions.couchace.core.api.CouchServer;
import org.tiogasolutions.couchace.core.api.request.CouchFeature;
import org.tiogasolutions.couchace.core.api.request.CouchFeatureSet;
import org.tiogasolutions.dev.common.DateUtils;
import org.tiogasolutions.lib.couchace.DefaultCouchServer;
import org.tiogasolutions.lib.couchace.support.CouchUtils;

import java.net.URI;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class TestFactory {

  static {
    CpCouchServer.DATABASE_NAME = "push-tests";
  }

  private static TestFactory SINGLETON;
  public static final ZoneId westCoastTimeZone = DateUtils.PDT;

  private final CpCouchServer couchServer;
  private final CpObjectMapper objectMapper;
  private final AccountStore accountStore;
  private final DomainStore domainStore;
  private final PushRequestStore pushRequestStore;

  public static TestFactory get() throws Exception {
    if (SINGLETON == null) {
      SINGLETON = new TestFactory();
    }
    return SINGLETON;
  }

  public TestFactory() throws Exception {

    objectMapper = new CpObjectMapper();

    CouchServer server = new DefaultCouchServer();
    CouchDatabase database = server.database(CpCouchServer.DATABASE_NAME, CouchFeatureSet.builder().add(CouchFeature.ALLOW_DB_DELETE, true).build());
    if (database.exists()) {
      database.deleteDatabase();
    }

    couchServer = new CpCouchServer();
    CouchUtils.createDatabase(database);
    CouchUtils.validateDesign(
      database,
      CpCouchServer.designNames,
      CpCouchServer.prefix,
      CpCouchServer.suffix);

    accountStore = new AccountStore(couchServer);
    domainStore = new DomainStore(couchServer);
    pushRequestStore = new PushRequestStore(couchServer);
  }

  public CpCouchServer getCouchServer() {
    return couchServer;
  }

  public CpObjectMapper getObjectMapper() {
    return objectMapper;
  }

  public AccountStore getAccountStore() {
    return accountStore;
  }

  public PushRequestStore getPushRequestStore() {
    return pushRequestStore;
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

  public Domain createDomain(Account account) {
    CreateDomainAction createClient = new CreateDomainAction(account, "some-key", "some-password");
    Domain domain = account.add(createClient);
    domainStore.create(domain);
    return domain;
  }

  public List<PushRequest> createPushRequests_Notifications(Domain domain) throws Exception {
    List<PushRequest> requests = new ArrayList<>();

    Push push = LqNotificationPush.newPush("Something bad happened", null, "test:true", "boy:girl", "color:red");
    PushRequest pushRequest = new PushRequest(AppContext.CURRENT_API_VERSION, domain, push);
    pushRequestStore.create(pushRequest);
    requests.add(pushRequest);

    return requests;
  }

  public List<PushRequest> createPushRequests_Emails(Domain domain) throws Exception {
    List<PushRequest> requests = new ArrayList<>();

    Push push = SmtpEmailPush.newPush("to@example.com", "from@example.com", "This is the subject", "<html><body><h1>Hello World</h1></body></html>", null);
    PushRequest pushRequest = new PushRequest(AppContext.CURRENT_API_VERSION, domain, push);
    pushRequestStore.create(pushRequest);
    requests.add(pushRequest);

    return requests;
  }

  public List<PushRequest> createPushRequests(Domain domain) throws Exception {
    Set<PushRequest> requests = new TreeSet<>();

    requests.addAll(createPushRequests_Emails(domain));
    requests.addAll(createPushRequests_Notifications(domain));

    return new ArrayList<>(requests);
  }

  public PluginContext pluginContext(TestFactory testFactory) {
    return new PluginContext() {
      @Override public PushRequestStore getPushRequestStore() {
        return testFactory.getPushRequestStore();
      }
      @Override public DomainStore getDomainStore() { return testFactory.getDomainStore(); }
      @Override public CpObjectMapper getObjectMapper() {
        return testFactory.getObjectMapper();
      }
      @Override public CpCouchServer getCouchServer() {
        return testFactory.getCouchServer();
      }
      @Override public URI getBaseURI() {
        return URI.create("http://localhost:8080/push-server");
      }
      @Override public PushProcessor getPushProcessor() {
        return null;
      }
      @Override public AppContext getAppContext() { return null; }
      @Override public void setLastMessage(String message) {}
    };
  }

  public Session createSession() {
    return new Session(0, "test@example.com");
  }
}
