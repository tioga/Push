package org.tiogasolutions.push.common.system;

import org.tiogasolutions.push.jackson.CpJacksonModule;
import com.fasterxml.jackson.databind.Module;
import org.tiogasolutions.dev.jackson.TiogaJacksonModule;
import org.tiogasolutions.lib.couchace.DefaultCouchServer;

import java.util.Arrays;
import java.util.List;

public class CpCouchServer extends DefaultCouchServer {

  public static String DATABASE_NAME = "cosmic-push";

  public static final List<String> designNames = Arrays.asList("account", "push-request", "domain");
  public static final String prefix = "/push-server-common/design-docs/";
  public static final String suffix = "-design.json";

  public CpCouchServer() {
    super(new Module[]{
      new TiogaJacksonModule(),
      new CpJacksonModule()});
  }
}
