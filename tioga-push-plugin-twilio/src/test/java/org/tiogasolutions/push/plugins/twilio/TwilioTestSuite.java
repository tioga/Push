package org.tiogasolutions.push.plugins.twilio;

import ch.qos.logback.classic.Level;
import org.testng.annotations.BeforeSuite;
import org.tiogasolutions.dev.common.LogbackUtils;

public class TwilioTestSuite {

  @BeforeSuite
  public void beforeSuite() {
    LogbackUtils.initLogback(Level.WARN);
  }

}
