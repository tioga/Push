package org.tiogasolutions.push.kernel.execution;

import org.tiogasolutions.push.kernel.accounts.Account;
import org.tiogasolutions.push.kernel.clients.DomainProfileEntity;
import org.tiogasolutions.push.kernel.system.Session;

import javax.ws.rs.core.UriInfo;

public class ExecutionContext {

  private Session session;
  private Account account;
  private DomainProfileEntity domain;

  private final UriInfo uriInfo;

  public ExecutionContext(UriInfo uriInfo) {
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

  public void setLastMessage(String message) {
    if (session != null) {
      session.setLastMessage(message);
    }
  }
}
