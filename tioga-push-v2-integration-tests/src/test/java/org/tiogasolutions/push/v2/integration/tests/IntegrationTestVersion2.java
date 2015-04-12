package org.tiogasolutions.push.v2.integration.tests;

import org.testng.SkipException;
import org.tiogasolutions.push.pub.*;
import org.tiogasolutions.push.client.LiveCosmicPushClient;
import org.tiogasolutions.push.pub.common.PingPush;
import org.tiogasolutions.push.pub.common.PushResponse;
import org.tiogasolutions.push.pub.common.RequestStatus;
import org.tiogasolutions.push.test.TestFactory;
import org.tiogasolutions.dev.common.BeanUtils;
import org.tiogasolutions.dev.common.EnvUtils;
import org.tiogasolutions.dev.common.exceptions.ExceptionUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

@Test
public class IntegrationTestVersion2 {

  private TestFactory testFactory;
  private LiveCosmicPushClient gateway;
  private String callbackUrl = null;

  @BeforeClass
  public void beforeClass() throws Exception {
    testFactory = TestFactory.get();

    try {
      String url = "http://www.cosmicpush.com/api/v2";
      String username = EnvUtils.requireProperty("TIOGA_TEST_DOMAIN_NAME");
      String password = EnvUtils.requireProperty("TIOGA_TEST_DOMAIN_PASS");
      gateway = new LiveCosmicPushClient(url, username, password);

    } catch (Exception ex) {
      throw new SkipException("Authentication required for test.", ex);
    }
  }

  public void testNotificationPush() throws Exception {
    LqNotificationPush push = LqNotificationPush.newPush("integration-test", "Notice what I'm doing?", "tracking-id", callbackUrl, BeanUtils.toMap("unit-test:true"));
    PushResponse response = gateway.send(push);
    assertEquals(response.getRequestStatus(), RequestStatus.pending);

    push = LqNotificationPush.newPush("integration-test", "Now I want to share some info", "tracking-id", callbackUrl, BeanUtils.toMap("day:Sunday", "size:Large"));
    response = gateway.send(push);
    assertEquals(response.getRequestStatus(), RequestStatus.pending);

    String msg = ExceptionUtils.toString(new IllegalArgumentException("I think I might have broken it!"));
    push = LqNotificationPush.newPush("integration-test", "Something really bad happened here!", "tracking-id", callbackUrl, BeanUtils.toMap("priority:Urgent", "exception:" + msg));
    response = gateway.send(push);
    assertEquals(response.getRequestStatus(), RequestStatus.pending);
  }

  public void testXmppPush() throws Exception {
    XmppPush push = XmppPush.newPush("jacob.parr@gmail.com", "Are you there?", callbackUrl, BeanUtils.toMap("color:red", "test:yes"));
    PushResponse response = gateway.send(push);
    assertEquals(response.getRequestStatus(), RequestStatus.pending);
  }

  public void testTwilioSmsPush() throws Exception {
    TwilioSmsPush push = TwilioSmsPush.newPush("7745677277", "5596407277", "test message", "http://example.com/callback");
    PushResponse response = gateway.send(push);
    assertEquals(response.getRequestStatus(), RequestStatus.pending);
  }

  public void testSesEmailPush() throws Exception {
    SesEmailPush push = SesEmailPush.newPush(
        "Test Parr <test@jacobparr.com>",
        "Bot Parr <bot@jacobparr.com>",
        "This is a test", "<html><body>Are you there?</body></html>",
        callbackUrl, BeanUtils.toMap("unit-test:true"));

    PushResponse response = gateway.send(push);
    assertNotNull(response);
    assertEquals(response.getRequestStatus(), RequestStatus.pending);
  }

  public void testSmtpEmailPush() throws Exception {
    SmtpEmailPush push = SmtpEmailPush.newPush(
        "Test Parr <test@jacobparr.com>",
        "Bot Parr <bot@jacobparr.com>",
        "This is a test",
        "Are you there?",
        callbackUrl, BeanUtils.toMap("unit-test:true"));

    PushResponse response = gateway.send(push);
    assertNotNull(response);
    assertEquals(response.getRequestStatus(), RequestStatus.pending);
  }

  public void testEmailPush() throws Exception {
    EmailPush push = EmailPush.newPush(
        "Test Parr <test@jacobparr.com>",
        "Bot Parr <bot@jacobparr.com>",
        "This is a test",
        "Are you there?",
        callbackUrl, BeanUtils.toMap("unit-test:true"));

    PushResponse response = gateway.send(push);
    assertNotNull(response);
    assertEquals(response.getRequestStatus(), RequestStatus.pending);
  }

  public void testPingPush() throws Exception {
    long duration = gateway.ping();
    assertTrue(duration > 0);

    PushResponse response = gateway.send(PingPush.newPush());
    assertNotNull(response.getCreatedAt());
    assertNotNull(response.getDomainId());
    assertTrue(response.getNotes().isEmpty());
    assertNotNull(response.getPushRequestId());
    assertEquals(response.getRequestStatus(), RequestStatus.processed);
  }
}
