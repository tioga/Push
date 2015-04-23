package org.tiogasolutions.push.common;

import org.tiogasolutions.dev.common.EnvUtils;

public abstract class PushEnvUtils {

  public static String findContextRoot() {
    return EnvUtils.findProperty("push.server.context.root", "");
  }

}
