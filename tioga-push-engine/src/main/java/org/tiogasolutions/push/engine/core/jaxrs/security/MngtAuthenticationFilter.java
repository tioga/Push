package org.tiogasolutions.push.engine.core.jaxrs.security;

import org.tiogasolutions.push.engine.core.system.CpApplication;
import org.tiogasolutions.push.engine.core.resources.RootResource;
import org.tiogasolutions.push.common.accounts.Account;
import org.tiogasolutions.push.common.system.AppContext;
import org.tiogasolutions.push.common.system.Session;
import org.tiogasolutions.dev.common.exceptions.ApiException;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.io.IOException;
import java.net.URI;
import java.security.Principal;

@MngtAuthentication
@Priority(Priorities.AUTHENTICATION + 1)
public class MngtAuthenticationFilter implements ContainerRequestFilter {

  private final AppContext appContext;

  public MngtAuthenticationFilter(@Context Application application) {
    this.appContext = AppContext.from(application);
  }

  @Override
  public void filter(ContainerRequestContext requestContext) throws IOException {

    try {
      Session session = appContext.getSessionStore().getSession(requestContext);
      if (session == null) {
        throw ApiException.unauthorized();
      }

      String emailAddress = session.getEmailAddress();
      Account account = appContext.getAccountStore().getByEmail(emailAddress);

      if (account == null) {
        throw ApiException.unauthorized();
      }

      final SecurityContext securityContext = requestContext.getSecurityContext();
      requestContext.setSecurityContext(new MngtSecurityContext(securityContext, account));
      CpApplication.getExecutionContext().setAccount(account);

    } catch (ApiException e) {
      URI uri = requestContext.getUriInfo().getBaseUriBuilder().queryParam("r", RootResource.REASON_CODE_UNAUTHORIZED).build();
      Response response = Response.seeOther(uri).build();
      requestContext.abortWith(response);
    }
  }

  private static class MngtSecurityContext implements SecurityContext {
    private final boolean secure;
    private final Account account;
    public MngtSecurityContext(SecurityContext securityContext, Account account) {
      this.account = account;
      this.secure = securityContext.isSecure();
    }
    @Override public boolean isUserInRole(String role) {
      return false;
    }
    @Override public boolean isSecure() {
      return secure;
    }
    @Override public String getAuthenticationScheme() {
      return "FORM_AUTH";
    }
    @Override public Principal getUserPrincipal() {
      return account::getEmailAddress;
    }
  }

  public static class MngtThreadLocal extends ThreadLocal {

  }
}
