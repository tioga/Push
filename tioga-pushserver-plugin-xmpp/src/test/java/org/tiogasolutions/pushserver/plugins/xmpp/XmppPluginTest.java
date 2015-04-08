package org.tiogasolutions.pushserver.plugins.xmpp;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.net.URL;

@Test
public class XmppPluginTest {

    private XmppPlugin plugin;

    @BeforeClass
    public void beforeClass() throws Exception {
    }

    @Test
    public void iconUrlTest() {
      URL url = getClass().getResource("/org/tiogasolutions/pushserver/plugins/xmpp/icon-enabled.png");
      Assert.assertNotNull(url);

      url = getClass().getResource("/org/tiogasolutions/pushserver/plugins/xmpp/icon-disabled.png");
      Assert.assertNotNull(url);
    }
}