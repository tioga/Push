package org.tiogasolutions.pushserver.pub.internal;

import org.tiogasolutions.dev.common.id.uuid.TimeUuid;

public class CpIdGenerator {

  public static String newId() {
    return TimeUuid.randomUUID().toString();
  }
}
