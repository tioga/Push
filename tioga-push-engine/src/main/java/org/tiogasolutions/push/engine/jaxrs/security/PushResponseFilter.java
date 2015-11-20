package org.tiogasolutions.push.engine.jaxrs.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.tiogasolutions.push.kernel.execution.ExecutionManager;
import org.tiogasolutions.push.kernel.system.Session;
import org.tiogasolutions.push.kernel.system.SessionStore;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.NewCookie;
import java.io.IOException;
import java.util.Collections;

@PreMatching
@Priority(Priorities.AUTHENTICATION)
public class PushResponseFilter implements ContainerResponseFilter {

  private final SessionStore sessionStore;
  private final ExecutionManager executionManager;

  @Autowired
  public PushResponseFilter(ExecutionManager executionManager, SessionStore sessionStore) {
    this.sessionStore = sessionStore;
    this.executionManager = executionManager;
  }

  @Override
  public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
    responseContext.getHeaders().add("Access-Control-Allow-Origin", "*");
    responseContext.getHeaders().add("X-UA-Compatible", "IE=Edge");
    responseContext.getHeaders().add("p3p", "CP=\"Push server does not have a P3P policy. Learn why here: https://www.TiogaSolutions.com/push/static/p3p.html\"");

    Session session = executionManager.context().getSession();
    boolean valid = sessionStore.isValid(session);

    if (session != null && valid) {
      session.renew();
      NewCookie cookie = SessionStore.toCookie(requestContext.getUriInfo(), session);
      responseContext.getHeaders().put(HttpHeaders.SET_COOKIE, Collections.singletonList(cookie));
    }

    // Clear everything when we are all done.
    executionManager.removeExecutionContext();
  }
}
