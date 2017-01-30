package org.tiogasolutions.push.engine.jaxrs.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.tiogasolutions.dev.common.EqualsUtils;
import org.tiogasolutions.push.kernel.accounts.DomainStore;
import org.tiogasolutions.push.kernel.clients.DomainProfileEntity;
import org.tiogasolutions.push.kernel.execution.ExecutionManager;

import javax.annotation.Priority;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Principal;

@Provider
@ApiAuthentication
@Priority(Priorities.AUTHENTICATION + 1)
public class ApiAuthenticationFilter implements ContainerRequestFilter {

  private final DomainStore domainStore;
  private final ExecutionManager executionManager;

  @Autowired
  public ApiAuthenticationFilter(ExecutionManager executionManager, DomainStore domainStore) {
    this.domainStore = domainStore;
    this.executionManager = executionManager;
  }

  @Override
  public void filter(ContainerRequestContext requestContext) throws IOException {
    String authHeader = requestContext.getHeaderString("Authorization");

    if (authHeader == null) {
      throw new NotAuthorizedException("API");
    } else if (authHeader.startsWith("Basic ") == false) {
      throw new NotAuthorizedException("API");
    } else {
      authHeader = authHeader.substring(6);
    }

    byte[] bytes = DatatypeConverter.parseBase64Binary(authHeader);
    String basicAuth = new String(bytes, StandardCharsets.UTF_8);

    int pos = basicAuth.indexOf(":");

    String domainKey;
    String password;

    if (pos < 0) {
      domainKey = basicAuth;
      password = null;

    } else {
      domainKey = basicAuth.substring(0, pos);
      password = basicAuth.substring(pos+1);
    }

    DomainProfileEntity domainProfile = domainStore.getByDomainKey(domainKey);
    if (domainProfile == null) {
      throw new NotAuthorizedException("API");
    }

    if (EqualsUtils.objectsNotEqual(password, domainProfile.getDomainPassword())) {
      throw new NotAuthorizedException("API");
    }

    final SecurityContext securityContext = requestContext.getSecurityContext();
    requestContext.setSecurityContext(new ApiSecurityContext(securityContext, domainProfile));

    executionManager.getContext().setDomain(domainProfile);
  }

  private class ApiSecurityContext implements SecurityContext {
    private final boolean secure;
    private final DomainProfileEntity domain;
    public ApiSecurityContext(SecurityContext securityContext, DomainProfileEntity domain) {
      this.domain = domain;
      this.secure = securityContext.isSecure();
    }
    @Override public boolean isUserInRole(String role) {
      return false;
    }
    @Override public boolean isSecure() {
      return secure;
    }
    @Override public String getAuthenticationScheme() {
      return "BASIC_AUTH";
    }
    @Override public Principal getUserPrincipal() {
      return domain::getDomainKey;
    }
  }
}
