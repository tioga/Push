package org.tiogasolutions.pushserver.plugins.twilio;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.net.URL;

@Test
public class TwilioPluginTest {

    @Test
    public void iconUrlTest() throws Exception {
      URL url = getClass().getResource("/org/tiogasolutions/pushserver/plugins/twilio/icon-enabled.png");
      Assert.assertNotNull(url);

      url = getClass().getResource("/org/tiogasolutions/pushserver/plugins/twilio/icon-disabled.png");
      Assert.assertNotNull(url);
    }
}
