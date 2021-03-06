package org.tiogasolutions.push.client;

import org.tiogasolutions.push.pub.common.Push;
import org.tiogasolutions.push.pub.TwilioSmsPush;
import org.tiogasolutions.dev.common.ComparisonResults;
import org.tiogasolutions.dev.common.EqualsUtils;
import org.tiogasolutions.dev.common.json.JsonTranslator;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.tiogasolutions.push.pub.internal.PushUtils;

import java.net.InetAddress;

@Test
public class TwilioPushTranslationTest {

    private LivePushServerClient gateway = new LivePushServerClient("http://someone:password@example.com");
    private JsonTranslator translator = gateway.getClient().getTranslator();

    public void translateTwilioPush() throws Exception {
        Push originalPush = TwilioSmsPush.newPush("+15551112222", "+12221115555", "test message", "http://example.com/callback");
        String json = translator.toJson(originalPush);

        InetAddress remoteAddress = PushUtils.getLocalHost();
        String expected = String.format(EXPECTED_JSON, remoteAddress.getCanonicalHostName(), remoteAddress.getHostAddress());
        Assert.assertEquals(json, expected);

        Push translatedPush = translator.fromJson(TwilioSmsPush.class, json);
        ComparisonResults results = EqualsUtils.compare(originalPush, translatedPush);
        results.assertValidationComplete();
    }

    private static final String EXPECTED_JSON = "{\n" +
            "  \"pushType\" : \"twilio\",\n" +
            "  \"from\" : \"+15551112222\",\n" +
            "  \"recipient\" : \"+12221115555\",\n" +
            "  \"message\" : \"test message\",\n" +
            "  \"callbackUrl\" : \"http://example.com/callback\",\n" +
            "  \"remoteHost\" : \"%s\",\n" +
            "  \"remoteAddress\" : \"%s\",\n" +
            "  \"traits\" : { }\n" +
            "}";
}
