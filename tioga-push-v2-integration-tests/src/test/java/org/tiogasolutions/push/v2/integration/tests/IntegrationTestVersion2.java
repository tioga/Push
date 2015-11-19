package org.tiogasolutions.push.v2.integration.tests;

import org.testng.SkipException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.tiogasolutions.dev.common.BeanUtils;
import org.tiogasolutions.dev.common.EnvUtils;
import org.tiogasolutions.push.client.LivePushServerClient;
import org.tiogasolutions.push.pub.SesEmailPush;
import org.tiogasolutions.push.pub.SmtpEmailPush;
import org.tiogasolutions.push.pub.TwilioSmsPush;
import org.tiogasolutions.push.pub.XmppPush;
import org.tiogasolutions.push.pub.common.PingPush;
import org.tiogasolutions.push.pub.common.PushResponse;
import org.tiogasolutions.push.pub.common.RequestStatus;
import org.tiogasolutions.push.test.TestFactory;

import static org.testng.Assert.*;

@Test
public class IntegrationTestVersion2 {

  private TestFactory testFactory;
  private LivePushServerClient gateway;
  private String callbackUrl = null;

  @BeforeClass
  public void beforeClass() throws Exception {
    testFactory = new TestFactory(0);

    try {
      String url = "http://localhost:39009/push-server/client/api/v2";
      String username = EnvUtils.requireProperty("TIOGA_TEST_DOMAIN_NAME");
      String password = EnvUtils.requireProperty("TIOGA_TEST_DOMAIN_PASS");
      gateway = new LivePushServerClient(url, username, password);

    } catch (Exception ex) {
      throw new SkipException("Authentication required for test.", ex);
    }
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
