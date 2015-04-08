package org.tiogasolutions.pushserver.engine.grizzly;

import org.tiogasolutions.pushserver.engine.core.system.CpApplication;
import org.glassfish.jersey.server.ResourceConfig;

public class CpResourceConfig extends ResourceConfig {

  public CpResourceConfig(CpApplication application) {

    application.getClasses().forEach(this::register);

    application.getSingletons().forEach(this::register);

    addProperties(application.getProperties());
  }
}
