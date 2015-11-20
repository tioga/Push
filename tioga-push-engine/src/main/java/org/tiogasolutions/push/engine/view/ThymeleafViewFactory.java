package org.tiogasolutions.push.engine.view;

import java.net.URL;

public class ThymeleafViewFactory {
  public static final String TAIL = ".html";
  public static final String ROOT = "/push-server-app/view/";

  public static final String WELCOME = validate("welcome");

  public static final String MANAGE_ACCOUNT =           validate("manage/account");
  public static final String MANAGE_API_CLIENT =        validate("manage/domain");

  public static final String MANAGE_API_REQUESTS =      validate("manage/push-request");

  public static final String MANAGE_API_EMAIL =         validate("manage/push-email");
  public static final String MANAGE_API_EMAILS =        validate("manage/push-emails");

  private static String validate(String view) {
    String resource = ROOT+view+TAIL;
    URL url = ThymeleafViewFactory.class.getResource(resource);
    if (url == null) {
      String msg = String.format("The resource \"%s\" does not exist.", resource);
      throw new IllegalArgumentException(msg);
    }
    return view;
  }
}
