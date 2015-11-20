/*
 * Copyright (c) 2014 Jacob D. Parr
 *
 * This software may not be used without permission.
 */

package org.tiogasolutions.push.engine;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.tiogasolutions.dev.common.BeanUtils;
import org.tiogasolutions.dev.common.ComparisonResults;
import org.tiogasolutions.dev.common.EqualsUtils;
import org.tiogasolutions.dev.jackson.TiogaJacksonTranslator;
import org.tiogasolutions.push.jackson.CpObjectMapper;
import org.tiogasolutions.push.kernel.accounts.Account;
import org.tiogasolutions.push.kernel.clients.DomainProfileEntity;
import org.tiogasolutions.push.kernel.execution.ExecutionContext;
import org.tiogasolutions.push.kernel.execution.ExecutionManager;
import org.tiogasolutions.push.kernel.requests.PushRequest;
import org.tiogasolutions.push.kernel.requests.PushRequestStore;
import org.tiogasolutions.push.pub.SesEmailPush;
import org.tiogasolutions.push.pub.SmtpEmailPush;
import org.tiogasolutions.push.pub.XmppPush;
import org.tiogasolutions.push.pub.common.Push;
import org.tiogasolutions.push.test.AbstractSpringTest;
import org.tiogasolutions.push.test.TestFixture;

import java.net.InetAddress;

import static org.testng.Assert.assertEquals;

@Test
public class PushRequestTest extends AbstractSpringTest {

  @Autowired
  private TestFixture testFixture;

  @Autowired
  private ExecutionManager executionManager;

  @Autowired
  private PushRequestStore pushRequestStore;

  private String callbackUrl = "http://www.example.com/callback";

  // The test is really of the object mapper. We will use
  // a translator here just to make the work a little easier.
  private CpObjectMapper objectMapper = new CpObjectMapper();
  private TiogaJacksonTranslator translator = new TiogaJacksonTranslator(objectMapper);

  @BeforeMethod
  public void beforeMethod() throws Exception {
    ExecutionContext executionContext = executionManager.newContext(null);
    Account account = testFixture.createAccount();
    DomainProfileEntity domain = testFixture.createDomain(account);
    executionContext.setDomain(domain);
  }

  @AfterMethod
  public void afterMethod() throws Exception {
    executionManager.removeExecutionContext();
  }

  public void testCreate() throws Exception {

    Account account = testFixture.createAccount();
    DomainProfileEntity domain = testFixture.createDomain(account);

    SmtpEmailPush smtpEmailPush = SmtpEmailPush.newPush(
        "from", "to", "subject", "the HTML content",
        callbackUrl, BeanUtils.toMap("unit-test:true"));
    PushRequest request = new PushRequest(Push.CURRENT_API_VERSION, domain, smtpEmailPush);
    pushRequestStore.create(request);

    SesEmailPush sesEmailPush = SesEmailPush.newPush(
        "from", "to", "subject", "the HTML content",
        callbackUrl, BeanUtils.toMap("unit-test:true"));
    request = new PushRequest(Push.CURRENT_API_VERSION, domain, sesEmailPush);
    pushRequestStore.create(request);

    XmppPush imPush = XmppPush.newPush("recipient", "some message", callbackUrl, "color:red");
    request = new PushRequest(Push.CURRENT_API_VERSION, domain, imPush);
    pushRequestStore.create(request);

  }

  public void testTranslateSmtpEmailPush() throws Exception {

    Account account = testFixture.createAccount();
    DomainProfileEntity domain = testFixture.createDomain(account);

    Push push = SmtpEmailPush.newPush(
        "mickey.mouse@disney.com",
        "donald.duck@disney.com",
        "This is the subject",
        "<html><body><h1>Hello World</h1>So, how's it going?</body></html>",
        callbackUrl, "test:true", "type:email");

    InetAddress remoteAddress = InetAddress.getLocalHost();
    PushRequest oldPushRequest = new PushRequest(Push.CURRENT_API_VERSION, domain, push);
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

    Account account = testFixture.createAccount();
    DomainProfileEntity domain = testFixture.createDomain(account);

    Push push = SesEmailPush.newPush(
        "mickey.mouse@disney.com",
        "donald.duck@disney.com",
        "This is the subject",
        "<html><body><h1>Hello World</h1>So, how's it going?</body></html>",
        callbackUrl, "test:true", "type:email");

    InetAddress remoteAddress = InetAddress.getLocalHost();
    PushRequest oldPushRequest = new PushRequest(Push.CURRENT_API_VERSION, domain, push);
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

    Account account = testFixture.createAccount();
    DomainProfileEntity domain = testFixture.createDomain(account);

    Push push = XmppPush.newPush(
      "mickey.mouse@disney.com",
      "Just calling to say hello",
      callbackUrl, BeanUtils.toMap("color:green"));

    InetAddress remoteAddress = InetAddress.getLocalHost();
    PushRequest oldPushRequest = new PushRequest(Push.CURRENT_API_VERSION, domain, push);
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
}
