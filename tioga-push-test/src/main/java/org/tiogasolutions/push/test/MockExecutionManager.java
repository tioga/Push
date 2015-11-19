package org.tiogasolutions.push.test;

import org.tiogasolutions.couchace.core.api.CouchServer;
import org.tiogasolutions.push.jackson.CpObjectMapper;
import org.tiogasolutions.push.kernel.accounts.AccountStore;
import org.tiogasolutions.push.kernel.accounts.DomainStore;
import org.tiogasolutions.push.kernel.config.CouchServersConfig;
import org.tiogasolutions.push.kernel.execution.ExecutionContext;
import org.tiogasolutions.push.kernel.execution.ExecutionManager;
import org.tiogasolutions.push.kernel.plugins.PushProcessor;
import org.tiogasolutions.push.kernel.requests.PushRequestStore;

import javax.ws.rs.core.UriInfo;

public class MockExecutionManager extends ExecutionManager {

  private final TestFactory testFactory;

  public MockExecutionManager(TestFactory testFactory) {
    this.testFactory = testFactory;
  }

  @Override
  public ExecutionContext newContext(UriInfo uriInfo) {

    ExecutionContext executionContext = new ExecutionContext(null, null) {
      @Override public PushRequestStore getPushRequestStore() { return testFactory.getPushRequestStore(); }
      @Override public DomainStore getDomainStore() { return testFactory.getDomainStore(); }
      @Override public CpObjectMapper getObjectMapper() { return testFactory.getObjectMapper(); }
      @Override public AccountStore getAccountStore() { return testFactory.getAccountStore(); }
      @Override public PushProcessor getPushProcessor() { return null; }
      @Override public void setLastMessage(String message) {}
    };

    assignContext(executionContext);
    return executionContext;
  }

  public CouchServersConfig getCouchServersConfig() {
    return testFactory.getCouchServersConfig();
  }

  public CouchServer getCouchServer() {
    return testFactory.getCouchServer();
  }
}
