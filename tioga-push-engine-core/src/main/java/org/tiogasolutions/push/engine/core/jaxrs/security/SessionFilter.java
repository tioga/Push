package org.tiogasolutions.push.engine.core.jaxrs.security;

import org.tiogasolutions.push.common.system.ExecutionContext;
import org.tiogasolutions.push.engine.core.system.CpApplication;
import org.tiogasolutions.push.common.accounts.Account;
import org.tiogasolutions.push.common.accounts.AccountStore;
import org.tiogasolutions.push.common.system.AppContext;
import org.tiogasolutions.push.common.system.Session;
import org.tiogasolutions.push.common.system.SessionStore;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.*;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.util.Arrays;

@PreMatching
@Priority(Priorities.AUTHENTICATION)
public class SessionFilter implements ContainerRequestFilter, ContainerResponseFilter {

  private final AppContext appContext;
  private final Application application;
  private final UriInfo uriInfo;
  private final HttpHeaders headers;

  public SessionFilter(@Context Application application,
                       @Context UriInfo uriInfo,
                       @Context HttpHeaders headers) {

    this.uriInfo = uriInfo;
    this.headers = headers;
    this.application = application;
    this.appContext = AppContext.from(application);
  }

  @Override
  public void filter(ContainerRequestContext requestContext) throws IOException {
    ExecutionContext execContext = CpApplication.getExecutionContext();

    // Before anything, make sure the execution
    // context has a reference to the application.
    execContext.setApplication(application);
    execContext.setUriInfo(uriInfo);
    execContext.setHeaders(headers);

    Session session = appContext.getSessionStore().getSession(requestContext);
    CpApplication.getExecutionContext().setSession(session);

    AccountStore accountStore = execContext.getAccountStore();

    if (session != null) {
      Account account = accountStore.getByEmail(session.getEmailAddress());
      execContext.setAccount(account);
    }
  }

  @Override
  public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
    Session session = CpApplication.getExecutionContext().getSession();
    boolean valid = appContext.getSessionStore().isValid(session);

    if (session != null && valid) {
      session.renew();
      NewCookie cookie = SessionStore.toCookie(requestContext.getUriInfo(), session);
      responseContext.getHeaders().put(HttpHeaders.SET_COOKIE, Arrays.asList(cookie));
    }

    // Clear everything when we are all done.
    CpApplication.removeExecutionContext();
  }
}
