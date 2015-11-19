package org.tiogasolutions.push.kernel.execution;

import org.springframework.beans.factory.BeanFactory;
import org.tiogasolutions.push.jackson.CpObjectMapper;
import org.tiogasolutions.push.kernel.accounts.Account;
import org.tiogasolutions.push.kernel.accounts.AccountStore;
import org.tiogasolutions.push.kernel.accounts.DomainStore;
import org.tiogasolutions.push.kernel.clients.DomainProfileEntity;
import org.tiogasolutions.push.kernel.plugins.PushProcessor;
import org.tiogasolutions.push.kernel.requests.PushRequestStore;
import org.tiogasolutions.push.kernel.system.Session;

import javax.ws.rs.core.UriInfo;

public class ExecutionContext {

  private Session session;
  private Account account;
  private DomainProfileEntity domain;

  private final UriInfo uriInfo;

  private final BeanFactory beanFactory;

  public ExecutionContext(BeanFactory beanFactory, UriInfo uriInfo) {
    this.beanFactory = beanFactory;
    this.uriInfo = uriInfo;
  }

  public void setSession(Session session) {
    this.session = session;
  }

  public Session getSession() {
    return (session != null) ? session : new Session(-1, "dummy-session");
  }

  public void setAccount(Account account) {
    this.account = account;
  }

  public Account getAccount() {
    return account;
  }

  public void setDomain(DomainProfileEntity domain) {
    this.domain = domain;
  }

  public DomainProfileEntity getDomain() {
    return domain;
  }

  public UriInfo getUriInfo() {
    return uriInfo;
  }

  public PushProcessor getPushProcessor() {
    return beanFactory.getBean(PushProcessor.class);
  }

  public CpObjectMapper getObjectMapper() {
    return beanFactory.getBean(CpObjectMapper.class);
  }

  public DomainStore getDomainStore() {
    return beanFactory.getBean(DomainStore.class);
  }

  public AccountStore getAccountStore() {
    return beanFactory.getBean(AccountStore.class);
  }

  public PushRequestStore getPushRequestStore() {
    return beanFactory.getBean(PushRequestStore.class);
  }

  public <T> T getBean(Class<T> type) {
    return beanFactory.getBean(type);
  }

  public void setLastMessage(String message) {
    if (session != null) {
      session.setLastMessage(message);
    }
  }
}
