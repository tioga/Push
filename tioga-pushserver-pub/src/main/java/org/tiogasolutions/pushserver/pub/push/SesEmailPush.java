package org.tiogasolutions.pushserver.pub.push;

import org.tiogasolutions.pushserver.pub.common.CommonEmail;
import org.tiogasolutions.pushserver.pub.common.Push;
import org.tiogasolutions.pushserver.pub.common.PushType;
import org.tiogasolutions.pushserver.pub.internal.PushUtils;
import org.tiogasolutions.pushserver.pub.internal.RequestErrors;
import org.tiogasolutions.pushserver.pub.internal.ValidationUtils;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.tiogasolutions.dev.common.BeanUtils;
import org.tiogasolutions.dev.common.ReflectUtils;
import org.tiogasolutions.dev.common.StringUtils;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.LinkedHashMap;
import java.util.Map;

public class SesEmailPush implements CommonEmail, Push, Serializable {

  public static final PushType PUSH_TYPE = new PushType(SesEmailPush.class, "ses-email", "ASES E-Mail");

  private final String fromAddress;
  private final String toAddress;

  private final String emailSubject;
  private final String htmlContent;

  private final LinkedHashMap<String,String> traits = new LinkedHashMap<>();

  private final String remoteHost;
  private final String remoteAddress;

  private final String callbackUrl;

  private SesEmailPush(@JsonProperty("toAddress") String toAddress,
                       @JsonProperty("fromAddress") String fromAddress,
                       @JsonProperty("emailSubject") String emailSubject,
                       @JsonProperty("htmlContent") String htmlContent,
                       @JsonProperty("callbackUrl") String callbackUrl,
                       @JsonProperty("remoteHost") String remoteHost,
                       @JsonProperty("remoteAddress") String remoteAddress,
                       @JsonProperty("traits") Map<String, String> traits) {

    this.toAddress =   toAddress;
    this.fromAddress = fromAddress;

    this.emailSubject = emailSubject;

    String content = StringUtils.getTagContents(htmlContent, "body", 0);
    this.htmlContent = StringUtils.isNotBlank(content) ? content : htmlContent;

    this.callbackUrl = callbackUrl;

    this.remoteHost = remoteHost;
    this.remoteAddress = remoteAddress;

    // Get a list of all the keys so that we can loop on the map
    // and remove anything without an actual value (purge nulls).
    if (traits != null) {
      this.traits.putAll(traits);
    }
    String[] keys = ReflectUtils.toArray(String.class, this.traits.keySet());

    for (String key : keys) {
      if (StringUtils.isBlank(this.traits.get(key))) {
        this.traits.remove(key);
      }
    }
  }

  @Override
  public PushType getPushType() {
    return PUSH_TYPE;
  }

  @Override
  public String getRemoteHost() {
    return remoteHost;
  }

  @Override
  public String getRemoteAddress() {
    return remoteAddress;
  }

  public String getFromAddress() {
    return fromAddress;
  }

  public String getToAddress() {
    return toAddress;
  }

  public String getEmailSubject() {
    return emailSubject;
  }

  public String getHtmlContent() {
    return htmlContent;
  }

  @Override
  public String getCallbackUrl() {
    return callbackUrl;
  }

  @Override
  public Map<String, String> getTraits() {
    return traits;
  }

  @Override
  public RequestErrors validate(RequestErrors errors) {
    ValidationUtils.requireValue(errors, toAddress, "The \"to\" address must be specified.");
    ValidationUtils.requireValue(errors, fromAddress, "The \"from\" address must be specified.");

    if (StringUtils.isBlank(emailSubject) && StringUtils.isBlank(htmlContent)) {
      errors.add("At least the subject and/or the HTML content must be specified.");
    }

    return errors;
  }

  public static SesEmailPush newPush(String toAddress, String fromAddress,
                                     String emailSubject, String htmlContent,
                                     String callbackUrl, String... traits) {

    InetAddress remoteAddress = PushUtils.getLocalHost();
    return new SesEmailPush(toAddress, fromAddress, emailSubject, htmlContent, callbackUrl, remoteAddress.getCanonicalHostName(), remoteAddress.getHostAddress(), BeanUtils.toMap(traits));
  }

  public static SesEmailPush newPush(String toAddress, String fromAddress,
                                     String emailSubject, String htmlContent,
                                     String callbackUrl, Map<String, String> traits) {

    InetAddress remoteAddress = PushUtils.getLocalHost();
    return new SesEmailPush(toAddress, fromAddress, emailSubject, htmlContent, callbackUrl, remoteAddress.getCanonicalHostName(), remoteAddress.getHostAddress(), traits);
  }

  public static SesEmailPush newPush(EmailPush emailPush) {
    return new SesEmailPush(
      emailPush.getToAddress(),
      emailPush.getFromAddress(),
      emailPush.getEmailSubject(),
      emailPush.getHtmlContent(),
      emailPush.getCallbackUrl(),
      emailPush.getRemoteHost(),
      emailPush.getRemoteAddress(),
      emailPush.getTraits()
    );
  }
}
