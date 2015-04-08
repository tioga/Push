/*
 * Copyright (c) 2014 Jacob D. Parr
 *
 * This software may not be used without permission.
 */

package org.tiogasolutions.pushserver.common.requests;

import org.tiogasolutions.pushserver.common.clients.Domain;
import org.tiogasolutions.pushserver.pub.common.*;
import org.tiogasolutions.pushserver.pub.internal.CpIdGenerator;
import org.tiogasolutions.pushserver.pub.push.EmailPush;
import org.tiogasolutions.pushserver.pub.push.LqNotificationPush;
import org.tiogasolutions.pushserver.pub.push.SesEmailPush;
import org.tiogasolutions.pushserver.pub.push.SmtpEmailPush;
import org.tiogasolutions.couchace.annotations.CouchEntity;
import org.tiogasolutions.couchace.annotations.CouchId;
import org.tiogasolutions.couchace.annotations.CouchRevision;
import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.tiogasolutions.dev.common.DateUtils;
import org.tiogasolutions.dev.common.StringUtils;
import org.tiogasolutions.dev.common.exceptions.ExceptionUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@CouchEntity(PushRequestStore.PUSH_REQUEST_DESIGN_NAME)
public class PushRequest implements Comparable<PushRequest> {

  private final String pushRequestId;
  private final String revision;

  private final int apiVersion;

  private final String domainId;
  private final String domainKey;

  private final LocalDateTime createdAt;
  private RequestStatus requestStatus;

  private final String remoteHost;
  private final String remoteAddress;

  private final PushType pushType;

  private final List<String> notes = new ArrayList<>();

  private final Push push;

  @JsonCreator
  private PushRequest(
    @JacksonInject("pushRequestId") String pushRequestId,
    @JacksonInject("revision") String revision,

    @JsonProperty("apiVersion") int apiVersion,
    @JsonProperty("domainId") String domainId,
    @JsonProperty("domainKey") String domainKey,

    @JsonProperty("createdAt") LocalDateTime createdAt,
    @JsonProperty("requestStatus") RequestStatus requestStatus,

    @JsonProperty("remoteHost") String remoteHost,
    @JsonProperty("remoteAddress") String remoteAddress,

    @JsonProperty("pushType") PushType pushType,
    @JsonProperty("notes") List<String> notes,
    @JsonProperty("push") Push push) {

    this.pushRequestId = pushRequestId;
    this.revision = revision;

    this.apiVersion= apiVersion;
    this.domainId = domainId;
    this.domainKey = domainKey;

    this.createdAt = createdAt;
    this.requestStatus = requestStatus;

    this.remoteHost = remoteHost;
    this.remoteAddress = remoteAddress;

    this.pushType = pushType;

    this.notes.addAll(notes);

    this.push = push;
  }

  public PushRequest(int apiVersion, Domain domain, Push push) {
    this.pushRequestId = CpIdGenerator.newId();
    this.revision = null;

    this.apiVersion = apiVersion;
    this.domainId = domain.getDomainId();
    this.domainKey = domain.getDomainKey();

    this.createdAt = DateUtils.currentLocalDateTime();
    this.requestStatus = RequestStatus.pending;

    this.remoteHost = push.getRemoteHost();
    this.remoteAddress = push.getRemoteAddress();

    this.pushType = push.getPushType();

    this.push = push;
  }

  @CouchId
  public String getPushRequestId() {
    return pushRequestId;
  }

  @CouchRevision
  public String getRevision() {
    return revision;
  }

  public String getDomainId() {
    return domainId;
  }

  public int getApiVersion() {
    return apiVersion;
  }

  public String getDomainKey() {
    return domainKey;
  }

  public String getRemoteHost() {
    return remoteHost;
  }

  public String getRemoteAddress() {
    return remoteAddress;
  }

  public PushType getPushType() {
    return pushType;
  }

  public String getCreatedAt(String format) {
    return (createdAt == null) ? null : createdAt.format(DateTimeFormatter.ofPattern(format));
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public RequestStatus getRequestStatus() {
    return requestStatus;
  }

  public List<String> getNotes() {
    return Collections.unmodifiableList(notes);
  }
  public void addNote(String note) {
    notes.add(note);
  }

  public RequestStatus processed() {
    return processed(null);
  }

  public RequestStatus processed(String note) {
    this.requestStatus = RequestStatus.processed;
    this.notes.add("Request has been processed.");

    if (StringUtils.isNotBlank(note)) {
      this.notes.add(note);
    }

    return this.requestStatus;
  }

  public RequestStatus denyRequest(String reasonNotPermitted) {
    this.requestStatus = RequestStatus.denied;
    this.notes.add("Request denied: " + reasonNotPermitted);

    return this.requestStatus;
  }

  public RequestStatus failed(String message) {
    this.requestStatus = RequestStatus.failed;

    if (StringUtils.isBlank(message)) {
      this.notes.add("*** FAILURE - Reason unspecified **");
    } else {
      this.notes.add("*** FAILURE ***");
      this.notes.add(message);
    }

    return this.requestStatus;
  }

  public RequestStatus failed(Exception ex) {
    this.requestStatus = RequestStatus.failed;
    this.notes.add("*** FAILURE ***");

    for (Throwable throwable : ExceptionUtils.getRootCauses(ex)) {
      this.notes.add(ExceptionUtils.getMessage(throwable));
    }

    return this.requestStatus;
  }

  public RequestStatus warn(String message) {
    this.requestStatus = RequestStatus.warning;

    if (StringUtils.isBlank(message)) {
      this.notes.add("*** WARNING - Reason unspecified **");
    } else {
      this.notes.add("*** WARNING ***");
      this.notes.add(message);
    }

    return this.requestStatus;
  }

  public RequestStatus warn(Throwable e) {
    this.requestStatus = RequestStatus.warning;
    this.notes.add("*** WARNING ***");

    for (Throwable throwable : ExceptionUtils.getRootCauses(e)) {
      this.notes.add(ExceptionUtils.getMessage(throwable));
    }

    return this.requestStatus;
  }

  public Push getPush() {
    return push;
  }

  @JsonIgnore
  public EmailPush getEmailPush() {
    return (push instanceof EmailPush) ? (EmailPush)push : null;
  }

  @JsonIgnore
  public SesEmailPush getSesEmailPush() {
    return (push instanceof SesEmailPush) ? (SesEmailPush)push : null;
  }

  @JsonIgnore
  public SmtpEmailPush getSmtpEmailPush() {
    return (push instanceof SmtpEmailPush) ? (SmtpEmailPush)push : null;
  }

  @JsonIgnore
  public CommonEmail getCommonEmail() {
    return (push instanceof CommonEmail) ? (CommonEmail)push : null;
  }

  @JsonIgnore
  public LqNotificationPush getNotificationPush() {
    return (push instanceof LqNotificationPush) ? (LqNotificationPush)push : null;
  }

  public boolean equals(Object object) {
    if (object instanceof PushRequest) {
      PushRequest that = (PushRequest)object;
      return this.pushRequestId.equals(that.pushRequestId);
    }
    return false;
  }

  @Override
  public int compareTo(PushRequest that) {
    return this.createdAt.compareTo(that.createdAt);
  }

  public String toString() {
    return push.getPushType().getLabel() + ": " + push.toString();
  }

  @JsonIgnore
  public PushTraits getPushTraits() {
    return new PushTraits(pushRequestId, domainKey, push.getTraits());
  }
}
