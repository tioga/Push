/*
 * Copyright (c) 2014 Jacob D. Parr
 *
 * This software may not be used without permission.
 */
package org.tiogasolutions.pushserver.engine.core.resources;

import org.tiogasolutions.pushserver.engine.core.resources.api.ApiResourceV1;
import org.tiogasolutions.pushserver.engine.core.resources.api.ApiResourceV2;
import org.tiogasolutions.pushserver.engine.core.resources.manage.ManageResource;
import org.tiogasolutions.pushserver.engine.core.system.CpApplication;
import org.tiogasolutions.pushserver.engine.core.view.Thymeleaf;
import org.tiogasolutions.pushserver.engine.core.view.ThymeleafViewFactory;
import org.tiogasolutions.pushserver.common.accounts.Account;
import org.tiogasolutions.pushserver.common.clients.Domain;
import org.tiogasolutions.pushserver.common.requests.PushRequest;
import org.tiogasolutions.pushserver.common.system.ExecutionContext;
import org.tiogasolutions.pushserver.common.system.Session;
import org.tiogasolutions.pushserver.common.system.SessionStore;
import org.tiogasolutions.pushserver.pub.push.LqNotificationPush;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tiogasolutions.dev.common.EqualsUtils;
import org.tiogasolutions.dev.common.exceptions.ApiException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@Path("/")
public class RootResource extends RootResourceSupport {

  public static final int REASON_CODE_INVALID_USERNAME_OR_PASSWORD = -1;
  public static final int REASON_CODE_UNAUTHORIZED = -2;
  public static final int REASON_SIGNED_OUT = -3;

  private static final Log log = LogFactory.getLog(RootResource.class);

  private final ExecutionContext execContext = CpApplication.getExecutionContext();

  public RootResource() {
    log.info("Created ");
  }

  @Override
  public UriInfo getUriInfo() {
    return execContext.getUriInfo();
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
    return new Thymeleaf(execContext.getSession(), ThymeleafViewFactory.WELCOME, new WelcomeModel(execContext.getAccount(), message, username, password));
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

    Account account = execContext.getAccountStore().getByEmail(username);

    if (account == null || EqualsUtils.objectsNotEqual(account.getPassword(), password)) {
      execContext.getSessionStore().remove(sessionId);

      NewCookie sessionCookie = SessionStore.toCookie(getUriInfo(), null);
      URI other = getUriInfo().getBaseUriBuilder().queryParam("r", REASON_CODE_INVALID_USERNAME_OR_PASSWORD).build();
      return Response.seeOther(other).cookie(sessionCookie).build();
    }

    Session session = execContext.getSessionStore().newSession(username);

    NewCookie sessionCookie = SessionStore.toCookie(getUriInfo(), session);
    URI other = getUriInfo().getBaseUriBuilder().path("manage").build();
    return Response.seeOther(other).cookie(sessionCookie).build();
  }

  @GET
  @Path("/sign-out")
  @Produces(MediaType.TEXT_HTML)
  public Response signOut(@CookieParam(SessionStore.SESSION_COOKIE_NAME) String sessionId) throws Exception {
    if (sessionId != null) {
      execContext.getSessionStore().remove(sessionId);
    }
    NewCookie sessionCookie = SessionStore.toCookie(getUriInfo(), null);
    URI other = getUriInfo().getBaseUriBuilder().queryParam("r", REASON_SIGNED_OUT).build();
    return Response.seeOther(other).cookie(sessionCookie).build();
  }

  @Path("/api")
  public ApiResourceV1 getApiResourceV1() throws Exception {
    return new ApiResourceV1();
  }

  @Path("/api/v2")
  public ApiResourceV2 getApiResourceV2() throws Exception {
    return new ApiResourceV2();
  }

  @Path("/manage")
  public ManageResource getManageResource() {
    return new ManageResource();
  }

  @GET @Path("/q/{pushRequestId}")
  public Response resolveCallback(@PathParam("pushRequestId") String pushRequestId) throws URISyntaxException {

    PushRequest request = execContext.getPushRequestStore().getByPushRequestId(pushRequestId);
    if (request == null) {
      throw ApiException.notFound("API request not found for " + pushRequestId);
    }

    Domain domain = execContext.getDomainStore().getByDocumentId(request.getDomainId());
    if (domain == null) {
      throw ApiException.notFound("Domain not found for " + request.getDomainId());
    }

    if (LqNotificationPush.PUSH_TYPE.equals(request.getPushType())) {
      String path = String.format("manage/domain/%s/notifications/%s", domain.getDomainKey(), pushRequestId);
      return Response.seeOther(new URI(path)).build();
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

