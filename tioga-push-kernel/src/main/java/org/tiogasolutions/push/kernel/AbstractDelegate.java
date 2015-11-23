/*
 * Copyright (c) 2014 Jacob D. Parr
 *
 * This software may not be used without permission.
 */

package org.tiogasolutions.push.kernel;

import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.uri.internal.JerseyUriBuilder;
import org.tiogasolutions.dev.common.StringUtils;
import org.tiogasolutions.dev.common.exceptions.ExceptionUtils;
import org.tiogasolutions.push.jackson.PushObjectMapper;
import org.tiogasolutions.push.kernel.execution.ExecutionContext;
import org.tiogasolutions.push.kernel.requests.PushRequest;
import org.tiogasolutions.push.kernel.requests.PushRequestStore;
import org.tiogasolutions.push.pub.common.RequestStatus;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

public abstract class AbstractDelegate implements Runnable {

  protected abstract RequestStatus processRequest() throws Exception;

  protected final ExecutionContext executionContext;
  protected final PushObjectMapper objectMapper;
  protected final PushRequest pushRequest;
  protected final PushRequestStore pushRequestStore;

  protected AbstractDelegate(ExecutionContext executionContext, PushObjectMapper objectMapper, PushRequestStore pushRequestStore, PushRequest pushRequest) {
    this.pushRequest = pushRequest;
    this.objectMapper = objectMapper;
    this.pushRequestStore = pushRequestStore;
    this.executionContext = executionContext;
  }

  @Override
  public final void run() {
    execute(false);
  }

  public void retry() {
    execute(true);
  }

  public boolean execute(boolean retry) {
    boolean success = true;

    if (retry) {
      pushRequest.addNote("** WARNING ** the API Request is being reprocessed.");
    }

    try {
      processRequest();

    } catch (Exception ex) {
      success = failed(ex, false);
    }

    try {
      if (success) {
        processCallback();
      }
    } catch (Exception ex) {
      success = failed(ex, true);
    }

    pushRequestStore.update(pushRequest);

    return success;
  }

  private boolean failed(Exception ex, boolean warning) {
    executionContext.setLastMessage("Execution failed: " + ExceptionUtils.getMessage(ex));

    ex.printStackTrace();

    if (warning)  pushRequest.warn(ex);
    else pushRequest.failed(ex);

    return false;
  }

  private void processCallback() throws Exception {

    String callbackURL = pushRequest.getPush().getCallbackUrl();
    if (callbackURL == null) {
      pushRequest.addNote("Callback not processed - url not specified");
      return;
    }

    String userName = getUserName(callbackURL);
    String password = getPassword(callbackURL);
    callbackURL = stripAuthentication(callbackURL);

    Client client = ClientBuilder.newBuilder().build();
    UriBuilder uriBuilder = new JerseyUriBuilder().uri(callbackURL);

    pushRequest.addNote("Executing callback to " + callbackURL);

    String json = objectMapper.writeValueAsString(pushRequest);
    Invocation.Builder builder;

    if (userName != null) {
      builder = client.target(uriBuilder)
          .register(HttpAuthenticationFeature.basic(userName, password))
          .request(MediaType.APPLICATION_JSON_TYPE);

    } else {
      builder = client.target(uriBuilder)
          .request(MediaType.APPLICATION_JSON_TYPE);
    }

    Response jerseyResponse = builder.post(Entity.entity(json, MediaType.APPLICATION_JSON_TYPE));
    int status = jerseyResponse.getStatus();

    if (status / 100 == 2) {
      pushRequest.addNote("Callback completed: HTTP " + status);
    } else {
      pushRequest.warn("Callback failed: HTTP " + status);
    }
  }

  public static String getUserName(String callbackURL) {
    if (StringUtils.isBlank(callbackURL)) return null;

    int posA = callbackURL.indexOf("://");
    if (posA < 0) return null;

    int posB = callbackURL.indexOf("@", posA);
    if (posB < 0) return null;

    String auth = callbackURL.substring(posA+3, posB);
    int pos = auth.indexOf(":");

    return (pos < 0) ? auth : auth.substring(0, pos);
  }

  public static String getPassword(String callbackURL) {
    if (StringUtils.isBlank(callbackURL)) return null;

    int posA = callbackURL.indexOf("://");
    if (posA < 0) return null;

    int posB = callbackURL.indexOf("@", posA);
    if (posB < 0) return null;

    String auth = callbackURL.substring(posA+3, posB);
    int pos = auth.indexOf(":");

    return (pos < 0) ? null : auth.substring(pos+1);
  }

  public static String stripAuthentication(String callbackURL) {
    if (StringUtils.isBlank(callbackURL)) return callbackURL;

    int posA = callbackURL.indexOf("://");
    if (posA < 0) return callbackURL;

    int posB = callbackURL.indexOf("@", posA);
    if (posB < 0) return callbackURL;

    String left = callbackURL.substring(0, posA+3);
    String right = callbackURL.substring(posB+1);

    return left+right;
  }

  public void start() {
    new Thread(this).start();
  }
}
