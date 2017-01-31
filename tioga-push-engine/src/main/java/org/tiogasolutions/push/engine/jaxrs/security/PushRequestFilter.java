package org.tiogasolutions.push.engine.jaxrs.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.tiogasolutions.push.kernel.accounts.Account;
import org.tiogasolutions.push.kernel.accounts.AccountStore;
import org.tiogasolutions.push.kernel.execution.ExecutionContext;
import org.tiogasolutions.push.kernel.execution.ExecutionManager;
import org.tiogasolutions.push.kernel.system.Session;
import org.tiogasolutions.push.kernel.system.SessionStore;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
@PreMatching
@Priority(Priorities.AUTHENTICATION)
public class PushRequestFilter implements ContainerRequestFilter {

    @Context
    private UriInfo uriInfo;

    @Autowired
    private AccountStore accountStore;

    private final SessionStore sessionStore;
    private final ExecutionManager executionManager;

    @Autowired
    public PushRequestFilter(ExecutionManager executionManager, SessionStore sessionStore) {
        this.sessionStore = sessionStore;
        this.executionManager = executionManager;
    }

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        ExecutionContext executionContext = executionManager.newContext(uriInfo);

        Session session = sessionStore.getSession(requestContext);
        executionContext.setSession(session);

        if (session != null) {
            Account account = accountStore.getByEmail(session.getEmailAddress());
            executionContext.setAccount(account);
        }
    }
}
