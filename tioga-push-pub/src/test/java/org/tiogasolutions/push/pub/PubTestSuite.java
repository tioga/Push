package org.tiogasolutions.push.pub;

import ch.qos.logback.classic.Level;
import org.testng.annotations.BeforeSuite;
import org.tiogasolutions.dev.common.LogbackUtils;

public class PubTestSuite {

  @BeforeSuite
  public void beforeSuite() {
    LogbackUtils.initLogback(Level.WARN);
  }

}
