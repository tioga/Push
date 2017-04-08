package org.tiogasolutions.push.engine.jaxrs.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.tiogasolutions.push.kernel.config.SystemConfiguration;
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
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.Collections;

@Provider
@PreMatching
@Priority(Priorities.AUTHENTICATION)
public class PushResponseFilter implements ContainerResponseFilter {

    private final SessionStore sessionStore;
    private final ExecutionManager executionManager;

    @Autowired
    private SystemConfiguration systemConfiguration;

    @Autowired
    public PushResponseFilter(ExecutionManager executionManager, SessionStore sessionStore) {
        this.sessionStore = sessionStore;
        this.executionManager = executionManager;
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        responseContext.getHeaders().add("Access-Control-Allow-Origin", "*");
        responseContext.getHeaders().add("Access-Control-Allow-Headers", "Accept, Content-Type, Authorization, Access-Control-Allow-Origin");
        responseContext.getHeaders().add("Access-Control-Allow-Methods", "GET, DELETE, PUT, POST");
        responseContext.getHeaders().add("Access-Control-Allow-Credentials", "true");

        Session session = executionManager.getContext().getSession();
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
