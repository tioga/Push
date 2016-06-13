package org.tiogasolutions.push.kernel;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.xml.bind.DatatypeConverter;

/**
 * Temporarily moved to main source so that we can reuse it in other modules.
 */
@Component
@Profile("test")
public class TestFactory {

  public static String toHttpAuth(String username, String password) {
    byte[] value = (username + ":" + password).getBytes();
    return "Basic " + DatatypeConverter.printBase64Binary(value);
  }
}
