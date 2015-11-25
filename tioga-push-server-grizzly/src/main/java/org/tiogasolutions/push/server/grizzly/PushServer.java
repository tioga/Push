package org.tiogasolutions.push.server.grizzly;

import ch.qos.logback.classic.Level;
import org.slf4j.Logger;
import org.tiogasolutions.app.common.AppPathResolver;
import org.tiogasolutions.app.common.AppUtils;
import org.tiogasolutions.push.engine.system.PushApplication;
import org.tiogasolutions.runners.grizzly.GrizzlyServer;
import org.tiogasolutions.runners.grizzly.GrizzlyServerConfig;
import org.tiogasolutions.runners.grizzly.ShutdownUtils;
import org.tiogasolutions.runners.grizzly.spring.ApplicationResolver;
import org.tiogasolutions.runners.grizzly.spring.GrizzlySpringServer;
import org.tiogasolutions.runners.grizzly.spring.ServerConfigResolver;

import java.nio.file.Path;
import java.util.Arrays;

import static org.slf4j.LoggerFactory.getLogger;

public class PushServer {

  private static final Logger log = getLogger(PushServer.class);

  public static void main(String...args) throws Exception {

    // Priority #1, configure default logging levels. This will be
    // overridden later when/if the logback.xml is found and loaded.
    AppUtils.initLogback(Level.WARN);

    // Assume we want by default INFO on when & how the grizzly server
    // is started. Possibly overwritten by logback.xml if used.
    AppUtils.setLogLevel(Level.INFO, PushServer.class);
    AppUtils.setLogLevel(Level.INFO, GrizzlySpringServer.GRIZZLY_CLASSES);

    // Load the resolver which gives us common tools for identifying the
    // runtime & config directories, logback.xml, etc.
    AppPathResolver resolver = new AppPathResolver("push.");
    Path runtimeDir = resolver.resolveRuntimePath();
    Path configDir = resolver.resolveConfigDir(runtimeDir);

    // Re-init logback if we can find the logback.xml
    Path logbackFile = AppUtils.initLogback(configDir, "push.log.config", "logback.xml");

    // Locate the spring file for this app or use DEFAULT_SPRING_FILE from the classpath if one is not found.
    String springConfigPath = resolver.resolveSpringPath(configDir, null);
    String activeProfiles = resolver.resolveSpringProfiles(); // defaults to "hosted"

    boolean shuttingDown = Arrays.asList(args).contains("-shutdown");
    String action = (shuttingDown ? "Shutting down" : "Starting");

    log.info("{} server:\n" +
      "  *  Runtime Dir:  {}\n" +
      "  *  Config Dir:   {}\n" +
      "  *  Logback File: {}\n" +
      "  *  Spring Path ({}):  {}", action, runtimeDir, configDir, logbackFile, activeProfiles, springConfigPath);

    // Create an instance of the grizzly server.
    GrizzlySpringServer grizzlyServer = new GrizzlySpringServer(
      ServerConfigResolver.fromClass(GrizzlyServerConfig.class),
      ApplicationResolver.fromClass(PushApplication.class),
      activeProfiles,
      springConfigPath
    );

    grizzlyServer.packages("org.tiogasolutions.push");

    if (Arrays.asList(args).contains("-shutdown")) {
      ShutdownUtils.shutdownRemote(grizzlyServer.getConfig());
      log.warn("Shutting down server at {}:{}", grizzlyServer.getConfig().getHostName(), grizzlyServer.getConfig().getShutdownPort());
      System.exit(0);
      return;
    }

    // Lastly, start the server.
    grizzlyServer.start();
  }
}
