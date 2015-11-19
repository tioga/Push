/*
 * Copyright (c) 2014 Jacob D. Parr
 *
 * This software may not be used without permission.
 */
package org.tiogasolutions.push.engine.core.resources;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.tiogasolutions.dev.common.EqualsUtils;
import org.tiogasolutions.dev.common.exceptions.ApiException;
import org.tiogasolutions.push.engine.core.resources.api.ApiResourceV2;
import org.tiogasolutions.push.engine.core.resources.manage.ManageResource;
import org.tiogasolutions.push.engine.core.view.Thymeleaf;
import org.tiogasolutions.push.engine.core.view.ThymeleafViewFactory;
import org.tiogasolutions.push.kernel.accounts.Account;
import org.tiogasolutions.push.kernel.clients.DomainProfileEntity;
import org.tiogasolutions.push.kernel.execution.ExecutionManager;
import org.tiogasolutions.push.kernel.requests.PushRequest;
import org.tiogasolutions.push.kernel.system.PluginManager;
import org.tiogasolutions.push.kernel.system.Session;
import org.tiogasolutions.push.kernel.system.SessionStore;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@Path("/")
public class RootResource extends RootResourceSupport {

  public static final int REASON_CODE_INVALID_USERNAME_OR_PASSWORD = -1;
  public static final int REASON_CODE_UNAUTHORIZED = -2;
  public static final int REASON_SIGNED_OUT = -3;

  private static final Log log = LogFactory.getLog(RootResource.class);

  @Context
  private UriInfo uriInfo;

  @Autowired
  private ExecutionManager executionManager;

  @Autowired
  private SessionStore sessionStore;

  @Autowired
  private PluginManager pluginManager;

  public RootResource() {
    log.info("Created ");
  }

  @Override
  public UriInfo getUriInfo() {
    return uriInfo;
  }

  @GET
  @Produces(MediaType.TEXT_HTML)
  public Thymeleaf getWelcome(@QueryParam("r") int reasonCode, @QueryParam("username") String username, @QueryParam("password") String password) throws IOException {

    String message = "";
    if (REASON_CODE_INVALID_USERNAME_OR_PASSWORD == reasonCode) {
      message = "Invalid username or password";
    } else if (REASON_CODE_UNAUTHORIZED == reasonCode) {
      message = "Your session has expired";
    } else if (REASON_SIGNED_OUT == reasonCode) {
      message = "You have successfully signed out";
    }

    return new Thymeleaf(executionManager.context().getSession(), ThymeleafViewFactory.WELCOME, new WelcomeModel(executionManager.context().getAccount(), message, username, password));
  }

  public static class WelcomeModel {
    private final Account account;
    private final String message;
    private final String emailAddress;
    private final String password;
    public WelcomeModel(Account account, String message, String emailAddress, String password) {
      this.account = account;
      this.message = message;
      this.emailAddress = emailAddress;
      this.password = password;
    }
    public Account getAccount() { return account; }
    public String getMessage() { return message; }
    public String getEmailAddress() { return emailAddress; }
    public String getPassword() { return password; }
  }

  @POST
  @Path("/sign-in")
  @Produces(MediaType.TEXT_HTML)
  public Response signIn(@FormParam("username") String username, @FormParam("password") String password, @CookieParam(SessionStore.SESSION_COOKIE_NAME) String sessionId) throws Exception {

    Account account = executionManager.context().getAccountStore().getByEmail(username);

    if (account == null || EqualsUtils.objectsNotEqual(account.getPassword(), password)) {
      sessionStore.remove(sessionId);

      NewCookie sessionCookie = SessionStore.toCookie(getUriInfo(), null);
      URI other = getUriInfo().getBaseUriBuilder().queryParam("r", REASON_CODE_INVALID_USERNAME_OR_PASSWORD).build();
      return Response.seeOther(other).cookie(sessionCookie).build();
    }

    Session session = sessionStore.newSession(username);

    NewCookie sessionCookie = SessionStore.toCookie(getUriInfo(), session);
    URI other = getUriInfo().getBaseUriBuilder().path("manage").build();
    return Response.seeOther(other).cookie(sessionCookie).build();
  }

  @GET
  @Path("/sign-out")
  @Produces(MediaType.TEXT_HTML)
  public Response signOut(@CookieParam(SessionStore.SESSION_COOKIE_NAME) String sessionId) throws Exception {
    if (sessionId != null) {
      sessionStore.remove(sessionId);
    }
    NewCookie sessionCookie = SessionStore.toCookie(getUriInfo(), null);
    URI other = getUriInfo().getBaseUriBuilder().queryParam("r", REASON_SIGNED_OUT).build();
    return Response.seeOther(other).cookie(sessionCookie).build();
  }

  @Path("/client/api/v2")
  public ApiResourceV2 getApiResourceV2() throws Exception {
    return new ApiResourceV2(executionManager);
  }

  @Path("/manage")
  public ManageResource getManageResource() {
    return new ManageResource(executionManager, sessionStore, pluginManager, uriInfo);
  }

  @GET @Path("/q/{pushRequestId}")
  public Response resolveCallback(@PathParam("pushRequestId") String pushRequestId) throws URISyntaxException {

    PushRequest request = executionManager.context().getPushRequestStore().getByPushRequestId(pushRequestId);
    if (request == null) {
      throw ApiException.notFound("API request not found for " + pushRequestId);
    }

    DomainProfileEntity domain = executionManager.context().getDomainStore().getByDocumentId(request.getDomainId());
    if (domain == null) {
      throw ApiException.notFound("Domain not found for " + request.getDomainId());
    }

    return Response.seeOther(getUriInfo().getBaseUriBuilder().build()).build();
  }

  @GET @Path("/health-check")
  @Produces(MediaType.TEXT_HTML)
  public Response healthCheck$GET() {
    return Response.status(Response.Status.OK).build();
  }

  @GET @Path("/privacy-policy")
  @Produces(MediaType.TEXT_HTML)
  public Thymeleaf privacyPolicy() {
    throw new UnsupportedOperationException();
    // return new Thymeleaf("/mun-mon/general/privacy-policy.jsp");
  }

  @GET @Path("/terms-of-service")
  @Produces(MediaType.TEXT_HTML)
  public Thymeleaf termsOfService() {
    throw new UnsupportedOperationException();
    // return new Thymeleaf("/mun-mon/general/terms-of-service.jsp");
  }

  @GET // TODO - implement the faq.jsp page.
  @Path("{resource: (faq\\.).* }")
  @Produces(MediaType.TEXT_HTML)
  public Thymeleaf getFaq() {
    throw new UnsupportedOperationException();
    // return new Thymeleaf("/mun-mon/faq.jsp");
  }

  @GET // TODO - implement the contact.jsp page.
  @Path("{resource: (contact\\.).* }")
  @Produces(MediaType.TEXT_HTML)
  public Thymeleaf getContact() {
    throw new UnsupportedOperationException();
    // return new Thymeleaf("/mun-mon/contact.jsp");
  }
}

