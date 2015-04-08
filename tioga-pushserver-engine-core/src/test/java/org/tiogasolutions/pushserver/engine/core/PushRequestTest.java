/*
 * Copyright (c) 2014 Jacob D. Parr
 *
 * This software may not be used without permission.
 */

package org.tiogasolutions.pushserver.engine.core;

import org.tiogasolutions.pushserver.common.accounts.Account;
import org.tiogasolutions.pushserver.common.clients.Domain;
import org.tiogasolutions.pushserver.common.requests.PushRequest;
import org.tiogasolutions.pushserver.common.requests.PushRequestStore;
import org.tiogasolutions.pushserver.common.system.AppContext;
import org.tiogasolutions.pushserver.jackson.CpObjectMapper;
import org.tiogasolutions.pushserver.pub.common.Push;
import org.tiogasolutions.pushserver.pub.push.XmppPush;
import org.tiogasolutions.pushserver.pub.push.LqNotificationPush;
import org.tiogasolutions.pushserver.pub.push.SesEmailPush;
import org.tiogasolutions.pushserver.pub.push.SmtpEmailPush;
import org.tiogasolutions.pushserver.test.TestFactory;
import org.tiogasolutions.dev.common.BeanUtils;
import org.tiogasolutions.dev.common.ComparisonResults;
import org.tiogasolutions.dev.common.EqualsUtils;
import org.tiogasolutions.dev.jackson.TiogaJacksonTranslator;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.net.InetAddress;

import static org.testng.Assert.assertEquals;

@Test
public class PushRequestTest {

  private TestFactory testFactory;

  private String callbackUrl = "http://www.example.com/callback";

  // The test is really of the object mapper. We will use
  // a translator here just to make the work a little easier.
  private CpObjectMapper objectMapper = new CpObjectMapper();
  private TiogaJacksonTranslator translator = new TiogaJacksonTranslator(objectMapper);

  private PushRequestStore pushRequestStore;

  @BeforeClass
  public void beforeClass() throws Exception {
    testFactory = TestFactory.get();
    this.pushRequestStore = testFactory.getPushRequestStore();
  }

  public void testCreate() throws Exception {

    Account account = testFactory.createAccount();
    Domain domain = testFactory.createDomain(account);

    SmtpEmailPush smtpEmailPush = SmtpEmailPush.newPush(
        "from", "to", "subject", "the HTML content",
        callbackUrl, BeanUtils.toMap("unit-test:true"));
    PushRequest request = new PushRequest(AppContext.CURRENT_API_VERSION, domain, smtpEmailPush);
    pushRequestStore.create(request);

    SesEmailPush sesEmailPush = SesEmailPush.newPush(
        "from", "to", "subject", "the HTML content",
        callbackUrl, BeanUtils.toMap("unit-test:true"));
    request = new PushRequest(AppContext.CURRENT_API_VERSION, domain, sesEmailPush);
    pushRequestStore.create(request);

    XmppPush imPush = XmppPush.newPush("recipient", "some message", callbackUrl, "color:red");
    request = new PushRequest(AppContext.CURRENT_API_VERSION, domain, imPush);
    pushRequestStore.create(request);

    LqNotificationPush lqNotificationPush = LqNotificationPush.newPush("unit-test", "summary", "tracking-id", callbackUrl, BeanUtils.toMap("test:true"));
    request = new PushRequest(AppContext.CURRENT_API_VERSION, domain, lqNotificationPush);
    pushRequestStore.create(request);

  }

  public void testTranslateSmtpEmailPush() throws Exception {

    Account account = testFactory.createAccount();
    Domain domain = testFactory.createDomain(account);

    Push push = SmtpEmailPush.newPush(
        "mickey.mouse@disney.com",
        "donald.duck@disney.com",
        "This is the subject",
        "<html><body><h1>Hello World</h1>So, how's it going?</body></html>",
        callbackUrl, "test:true", "type:email");

    InetAddress remoteAddress = InetAddress.getLocalHost();
    PushRequest oldPushRequest = new PushRequest(AppContext.CURRENT_API_VERSION, domain, push);
    String json = translator.toJson(oldPushRequest);

    String expected = String.format("{\n" +
        "  \"apiVersion\" : 2,\n" +
        "  \"domainId\" : \"%s\",\n" +
        "  \"domainKey\" : \"some-key\",\n" +
        "  \"createdAt\" : \"%s\",\n" +
        "  \"requestStatus\" : \"pending\",\n" +
        "  \"remoteHost\" : \"%s\",\n" +
        "  \"remoteAddress\" : \"%s\",\n" +
        "  \"pushType\" : \"smtp-email\",\n" +
        "  \"notes\" : [ ],\n" +
        "  \"push\" : {\n" +
        "    \"pushType\" : \"smtp-email\",\n" +
        "    \"toAddress\" : \"mickey.mouse@disney.com\",\n" +
        "    \"fromAddress\" : \"donald.duck@disney.com\",\n" +
        "    \"emailSubject\" : \"This is the subject\",\n" +
        "    \"htmlContent\" : \"<h1>Hello World</h1>So, how's it going?\",\n" +
        "    \"callbackUrl\" : \"http://www.example.com/callback\",\n" +
        "    \"remoteHost\" : \"%s\",\n" +
        "    \"remoteAddress\" : \"%s\",\n" +
        "    \"traits\" : {\n" +
        "      \"test\" : \"true\",\n" +
        "      \"type\" : \"email\"\n" +
        "    }\n" +
        "  },\n" +
        "  \"pushRequestId\" : \"%s\",\n" +
        "  \"revision\" : null\n" +
        "}",
        domain.getDomainId(), oldPushRequest.getCreatedAt(),
        remoteAddress.getCanonicalHostName(), remoteAddress.getHostAddress(),
        remoteAddress.getCanonicalHostName(), remoteAddress.getHostAddress(),
        oldPushRequest.getPushRequestId());

    assertEquals(json, expected);

    PushRequest newPushRequest = translator.fromJson(PushRequest.class, json);
    ComparisonResults results = EqualsUtils.compare(newPushRequest, oldPushRequest);
    results.assertValidationComplete();
  }

  public void testTranslateEmailPush() throws Exception {

    Account account = testFactory.createAccount();
    Domain domain = testFactory.createDomain(account);

    Push push = SesEmailPush.newPush(
        "mickey.mouse@disney.com",
        "donald.duck@disney.com",
        "This is the subject",
        "<html><body><h1>Hello World</h1>So, how's it going?</body></html>",
        callbackUrl, "test:true", "type:email");

    InetAddress remoteAddress = InetAddress.getLocalHost();
    PushRequest oldPushRequest = new PushRequest(AppContext.CURRENT_API_VERSION, domain, push);
    String json = translator.toJson(oldPushRequest);

    String expected = String.format("{\n" +
        "  \"apiVersion\" : 2,\n" +
        "  \"domainId\" : \"%s\",\n" +
        "  \"domainKey\" : \"some-key\",\n" +
        "  \"createdAt\" : \"%s\",\n" +
        "  \"requestStatus\" : \"pending\",\n" +
        "  \"remoteHost\" : \"%s\",\n" +
        "  \"remoteAddress\" : \"%s\",\n" +
        "  \"pushType\" : \"ses-email\",\n" +
        "  \"notes\" : [ ],\n" +
        "  \"push\" : {\n" +
        "    \"pushType\" : \"ses-email\",\n" +
        "    \"toAddress\" : \"mickey.mouse@disney.com\",\n" +
        "    \"fromAddress\" : \"donald.duck@disney.com\",\n" +
        "    \"emailSubject\" : \"This is the subject\",\n" +
        "    \"htmlContent\" : \"<h1>Hello World</h1>So, how's it going?\",\n" +
        "    \"callbackUrl\" : \"http://www.example.com/callback\",\n" +
        "    \"remoteHost\" : \"%s\",\n" +
        "    \"remoteAddress\" : \"%s\",\n" +
        "    \"traits\" : {\n" +
        "      \"test\" : \"true\",\n" +
        "      \"type\" : \"email\"\n" +
        "    }\n" +
        "  },\n" +
        "  \"pushRequestId\" : \"%s\",\n" +
        "  \"revision\" : null\n" +
        "}",
        domain.getDomainId(), oldPushRequest.getCreatedAt(),
        remoteAddress.getCanonicalHostName(), remoteAddress.getHostAddress(),
        remoteAddress.getCanonicalHostName(), remoteAddress.getHostAddress(),
        oldPushRequest.getPushRequestId());

    assertEquals(json, expected);

    PushRequest newPushRequest = translator.fromJson(PushRequest.class, json);
    ComparisonResults results = EqualsUtils.compare(newPushRequest, oldPushRequest);
    results.assertValidationComplete();
  }

  public void testTranslateImPush() throws Exception {

    Account account = testFactory.createAccount();
    Domain domain = testFactory.createDomain(account);

    Push push = XmppPush.newPush(
      "mickey.mouse@disney.com",
      "Just calling to say hello",
      callbackUrl, BeanUtils.toMap("color:green"));

    InetAddress remoteAddress = InetAddress.getLocalHost();
    PushRequest oldPushRequest = new PushRequest(AppContext.CURRENT_API_VERSION, domain, push);
    String json = translator.toJson(oldPushRequest);

    String expected = String.format("{\n" +
        "  \"apiVersion\" : 2,\n" +
        "  \"domainId\" : \"%s\",\n" +
        "  \"domainKey\" : \"some-key\",\n" +
        "  \"createdAt\" : \"%s\",\n" +
        "  \"requestStatus\" : \"pending\",\n" +
        "  \"remoteHost\" : \"%s\",\n" +
        "  \"remoteAddress\" : \"%s\",\n" +
        "  \"pushType\" : \"xmpp\",\n" +
        "  \"notes\" : [ ],\n" +
        "  \"push\" : {\n" +
        "    \"pushType\" : \"xmpp\",\n" +
        "    \"recipient\" : \"mickey.mouse@disney.com\",\n" +
        "    \"message\" : \"Just calling to say hello\",\n" +
        "    \"callbackUrl\" : \"http://www.example.com/callback\",\n" +
        "    \"remoteHost\" : \"%s\",\n" +
        "    \"remoteAddress\" : \"%s\",\n" +
        "    \"traits\" : {\n" +
        "      \"color\" : \"green\"\n" +
        "    }\n"+
        "  },\n" +
        "  \"pushRequestId\" : \"%s\",\n" +
        "  \"revision\" : null\n" +
        "}",
        domain.getDomainId(), oldPushRequest.getCreatedAt(),
        remoteAddress.getCanonicalHostName(), remoteAddress.getHostAddress(),
        remoteAddress.getCanonicalHostName(), remoteAddress.getHostAddress(),
        oldPushRequest.getPushRequestId());

    assertEquals(json, expected);

    PushRequest newPushRequest = translator.fromJson(PushRequest.class, json);
    ComparisonResults results = EqualsUtils.compare(newPushRequest, oldPushRequest);
    results.assertValidationComplete();
  }

  public void testNotificationPush() throws Exception {

    Account account = testFactory.createAccount();
    Domain domain = testFactory.createDomain(account);

    LqNotificationPush push = LqNotificationPush.newPush(
      "unit-test", "Hey, you need to check this out.", "some-tracking-id",
      callbackUrl, "test:true", "type:warning");

    InetAddress remoteAddress = InetAddress.getLocalHost();
    PushRequest oldPushRequest = new PushRequest(AppContext.CURRENT_API_VERSION, domain, push);
    String json = translator.toJson(oldPushRequest);

    String expected = String.format("{\n" +
        "  \"apiVersion\" : 2,\n" +
        "  \"domainId\" : \"%s\",\n" +
        "  \"domainKey\" : \"some-key\",\n" +
        "  \"createdAt\" : \"%s\",\n" +
        "  \"requestStatus\" : \"pending\",\n" +
        "  \"remoteHost\" : \"%s\",\n" +
        "  \"remoteAddress\" : \"%s\",\n" +
        "  \"pushType\" : \"liquid-notification\",\n" +
        "  \"notes\" : [ ],\n" +
        "  \"push\" : {\n" +
        "    \"pushType\" : \"liquid-notification\",\n" +
        "    \"topic\" : \"unit-test\",\n" +
        "    \"summary\" : \"Hey, you need to check this out.\",\n" +
        "    \"trackingId\" : \"some-tracking-id\",\n" +
        "    \"createdAt\" : \""+push.getCreatedAt()+"\",\n" +
        "    \"exceptionInfo\" : null,\n" +
        "    \"callbackUrl\" : \"http://www.example.com/callback\",\n" +
        "    \"remoteHost\" : \"%s\",\n" +
        "    \"remoteAddress\" : \"%s\",\n" +
        "    \"traits\" : {\n" +
        "      \"test\" : \"true\",\n" +
        "      \"type\" : \"warning\"\n" +
        "    },\n" +
        "    \"attachments\" : [ ]\n" +
        "  },\n" +
        "  \"pushRequestId\" : \"%s\",\n" +
        "  \"revision\" : null\n" +
        "}",
        domain.getDomainId(), oldPushRequest.getCreatedAt(),
        remoteAddress.getCanonicalHostName(), remoteAddress.getHostAddress(),
        remoteAddress.getCanonicalHostName(), remoteAddress.getHostAddress(),
        oldPushRequest.getPushRequestId());

    assertEquals(json, expected);

    PushRequest newPushRequest = translator.fromJson(PushRequest.class, json);
    ComparisonResults results = EqualsUtils.compare(newPushRequest, oldPushRequest);
    results.assertValidationComplete();
  }
}
