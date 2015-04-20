package org.tiogasolutions.push.common.system;

import org.tiogasolutions.push.common.accounts.AccountStore;
import org.tiogasolutions.push.common.accounts.DomainStore;
import org.tiogasolutions.push.common.requests.PushRequestStore;
import org.tiogasolutions.push.jackson.CpObjectMapper;
import org.tiogasolutions.apis.bitly.BitlyApis;

import javax.ws.rs.core.Application;

public class AppContext {

  public static final int CURRENT_API_VERSION = 2;

  private final BitlyApis bitlyApis;
  private final SessionStore sessionStore;
  private final CpObjectMapper objectMapper;
  private final DomainDatabaseConfig databaseConfig;
  private final AccountStore accountStore;
  private final PushRequestStore pushRequestStore;
  private final DomainStore domainStore;

  public AppContext(SessionStore sessionStore, CpObjectMapper objectMapper, String mainDbName, DomainDatabaseConfig databaseConfig, BitlyApis bitlyApis) {
    this.bitlyApis = bitlyApis;
    this.sessionStore = sessionStore;
    this.objectMapper = objectMapper;
    this.databaseConfig = databaseConfig;

    this.accountStore = new AccountStore(databaseConfig.getCouchServer(), mainDbName);
    this.domainStore = new DomainStore(databaseConfig.getCouchServer(), mainDbName);

    this.pushRequestStore = new PushRequestStore(databaseConfig);
  }

  public BitlyApis getBitlyApi() {
    return bitlyApis;
  }

  public CpObjectMapper getObjectMapper() {
    return objectMapper;
  }

  public SessionStore getSessionStore() {
    return sessionStore;
  }

  public AccountStore getAccountStore() {
    return accountStore;
  }

  public PushRequestStore getPushRequestStore() {
    return pushRequestStore;
  }

  public DomainDatabaseConfig getDatabaseConfig() {
    return databaseConfig;
  }

  public DomainStore getDomainStore() {
    return domainStore;
  }

  public static AppContext from(Application application) {
    Object object = application.getProperties().get(AppContext.class.getName());
    if (object == null) {
      throw new IllegalStateException("The application context has yet to be initialized.");
    } else if (AppContext.class.isInstance(object) == false) {
      String msg = String.format("The application object is not an instance of %s but rather %s.", AppContext.class.getName(), object.getClass().getName());
      throw new IllegalStateException(msg);
    }
    return (AppContext)object;
  }
}
