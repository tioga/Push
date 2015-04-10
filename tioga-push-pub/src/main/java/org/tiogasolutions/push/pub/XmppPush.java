/*
 * Copyright (c) 2014 Jacob D. Parr
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.tiogasolutions.push.pub;

import org.tiogasolutions.push.pub.common.Push;
import org.tiogasolutions.push.pub.common.PushType;
import org.tiogasolutions.push.pub.internal.PushUtils;
import org.tiogasolutions.push.pub.internal.RequestErrors;
import org.tiogasolutions.push.pub.internal.ValidationUtils;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.tiogasolutions.dev.common.BeanUtils;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.LinkedHashMap;
import java.util.Map;

@JsonIgnoreProperties({"imType"}) // For V1 compatibility
public class XmppPush implements Push, Serializable {

  public static PushType PUSH_TYPE = new PushType(XmppPush.class, "xmpp", "XMPP");

  private final String recipient;
  private final String message;
  private final Map<String,String> traits = new LinkedHashMap<>();

  private final String remoteHost;
  private final String remoteAddress;

  private final String callbackUrl;

  private XmppPush(@JsonProperty("recipient") String recipient,
                   @JsonProperty("message") String message,
                   @JsonProperty("callbackUrl") String callbackUrl,
                   @JsonProperty("remoteHost") String remoteHost,
                   @JsonProperty("remoteAddress") String remoteAddress,
                   @JsonProperty("traits") Map<String, String> traits) {

    this.recipient = recipient;
    this.message = message;
    this.callbackUrl = callbackUrl;

    this.remoteHost = remoteHost;
    this.remoteAddress = remoteAddress;

    if (traits != null) {
      this.traits.putAll(traits);
    }
  }

  @Override
  public String getRemoteHost() {
    return remoteHost;
  }

  @Override
  public String getRemoteAddress() {
    return remoteAddress;
  }

  @Override
  public String getCallbackUrl() {
    return callbackUrl;
  }

  @Override
  public PushType getPushType() {
    return PUSH_TYPE;
  }

  public String getRecipient() {
    return recipient;
  }

  public String getMessage() {
    return message;
  }

  @Override
  public RequestErrors validate(RequestErrors errors) {
    ValidationUtils.requireValue(errors, recipient, "The recipient must be specified.");
    ValidationUtils.requireValue(errors, message, "The chat message must be specified.");
    return errors;
  }

  @Override
  public Map<String, String> getTraits() {
    return traits;
  }

  public static XmppPush newPush(String recipient,
                                       String message,
                                       String callbackUrl,
                                       String...traits) {

    InetAddress remoteAddress = PushUtils.getLocalHost();
    return new XmppPush(
        recipient, message, callbackUrl,
        remoteAddress.getCanonicalHostName(), remoteAddress.getHostAddress(),
        BeanUtils.toMap(traits));
  }

  public static XmppPush newPush(String recipient,
                                       String message,
                                       String callbackUrl,
                                       Map<String,String> traits) {

    InetAddress remoteAddress = PushUtils.getLocalHost();
    return new XmppPush(
        recipient, message, callbackUrl,
        remoteAddress.getCanonicalHostName(), remoteAddress.getHostAddress(),
        traits);
  }
}
