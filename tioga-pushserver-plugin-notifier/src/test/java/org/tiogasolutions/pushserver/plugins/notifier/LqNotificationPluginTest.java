package org.tiogasolutions.pushserver.plugins.notifier;

import org.testng.annotations.Test;

import java.net.URL;

import static org.testng.Assert.assertNotNull;

@Test
public class LqNotificationPluginTest {

  public void testResources() throws Exception {
    URL url = getClass().getResource("/org/tiogasolutions/pushserver/plugins/notifier/icon-enabled.png");
    assertNotNull(url);

    url = getClass().getResource("/org/tiogasolutions/pushserver/plugins/notifier/icon-disabled.png");
    assertNotNull(url);

    url = getClass().getResource("/org/tiogasolutions/pushserver/plugins/notifier/admin.html");
    assertNotNull(url);

     url = getClass().getResource("/META-INF/services/org.tiogasolutions.pushserver.common.plugins.Plugin");
     assertNotNull(url);
  }
}