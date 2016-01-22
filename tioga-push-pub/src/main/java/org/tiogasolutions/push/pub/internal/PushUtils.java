package org.tiogasolutions.push.pub.internal;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class PushUtils {

  private PushUtils() {
  }

  public static InetAddress getLocalHost() {
    try {
      return InetAddress.getLocalHost();

    } catch (UnknownHostException e) {
      return getLocalHostByName();
    }
  }

  public static InetAddress getLocalHostByName() {
    try {
      return InetAddress.getByName("localhost");

    } catch (UnknownHostException e) {
      throw new RuntimeException("Exception getting InetAddress for localhost.", e);
    }
  }
}
