package org.tiogasolutions.push.engine.jaxrs.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.tiogasolutions.dev.common.exceptions.ApiException;
import org.tiogasolutions.push.engine.resources.RootResource;
import org.tiogasolutions.push.kernel.accounts.Account;
import org.tiogasolutions.push.kernel.accounts.AccountStore;
import org.tiogasolutions.push.kernel.execution.ExecutionManager;
import org.tiogasolutions.push.kernel.system.Session;
import org.tiogasolutions.push.kernel.system.SessionStore;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.net.URI;
import java.security.Principal;

@Provider
@MngtAuthentication
@Priority(Priorities.AUTHENTICATION + 1)
public class MngtAuthenticationFilter implements ContainerRequestFilter {

    private final SessionStore sessionStore;
    private final AccountStore accountStore;
    private final ExecutionManager executionManager;

    @Autowired
    public MngtAuthenticationFilter(ExecutionManager executionManager, SessionStore sessionStore, AccountStore accountStore) {
        this.sessionStore = sessionStore;
        this.accountStore = accountStore;
        this.executionManager = executionManager;
    }

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        try {
            Session session = sessionStore.getSession(requestContext);
            if (session == null) {
                throw ApiException.unauthorized();
            }

            String emailAddress = session.getEmailAddress();
            Account account = accountStore.getByEmail(emailAddress);

            if (account == null) {
                throw ApiException.unauthorized();
            }

            final SecurityContext securityContext = requestContext.getSecurityContext();
            requestContext.setSecurityContext(new MngtSecurityContext(securityContext, account));
            executionManager.getContext().setAccount(account);

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

        @Override
        public boolean isUserInRole(String role) {
            return false;
        }

        @Override
        public boolean isSecure() {
            return secure;
        }

        @Override
        public String getAuthenticationScheme() {
            return "FORM_AUTH";
        }

        @Override
        public Principal getUserPrincipal() {
            return account::getEmailAddress;
        }
    }
}
