package org.tiogasolutions.push.server.grizzly;

import ch.qos.logback.classic.Level;
import org.slf4j.Logger;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.tiogasolutions.app.common.AppPathResolver;
import org.tiogasolutions.app.common.AppUtils;
import org.tiogasolutions.runners.grizzly.GrizzlyServer;
import org.tiogasolutions.runners.grizzly.ShutdownUtils;

import java.nio.file.Path;

import static java.util.Arrays.asList;
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
    AppUtils.setLogLevel(Level.INFO, GrizzlyServer.GRIZZLY_CLASSES);

    // Load the resolver which gives us common tools for identifying the
    // runtime & config directories, logback.xml, etc.
    AppPathResolver resolver = new AppPathResolver("push.");
    Path runtimeDir = resolver.resolveRuntimePath();
    Path configDir = resolver.resolveConfigDir(runtimeDir);

    // Re-init logback if we can find the logback.xml
    Path logbackFile = AppUtils.initLogback(configDir, "push.log.config", "logback.xml");

    // Locate the spring file for this app or use DEFAULT_SPRING_FILE from the classpath if one is not found.
    String springConfigPath = resolver.resolveSpringPath(configDir, "classpath:/tioga-push-server-grizzly/spring-config.xml");
    String[] activeProfiles = resolver.resolveSpringProfiles(); // defaults to "hosted"

    boolean shuttingDown = asList(args).contains("-shutdown");
    String action = (shuttingDown ? "Shutting down" : "Starting");

    log.info("{} server:\n" +
      "  *  Runtime Dir     (push.runtime.dir)     {}\n" +
      "  *  Config Dir      (push.config.dir)      {}\n" +
      "  *  Logback File    (push.log.config)      {}\n" +
      "  *  Spring Path     (push.spring.config)   {}\n" +
      "  *  Active Profiles (push.active.profiles) {}", action, runtimeDir, configDir, logbackFile, springConfigPath, asList(activeProfiles));

    AbstractXmlApplicationContext applicationContext = createXmlConfigApplicationContext(springConfigPath, activeProfiles);

    GrizzlyServer grizzlyServer = applicationContext.getBean(GrizzlyServer.class);

    if (asList(args).contains("-shutdown")) {
      ShutdownUtils.shutdownRemote(grizzlyServer.getConfig());
      log.warn("Shutting down server at {}:{}", grizzlyServer.getConfig().getHostName(), grizzlyServer.getConfig().getShutdownPort());
      System.exit(0);
      return;
    }

    // Lastly, start the server.
    grizzlyServer.start();
  }

  public static AbstractXmlApplicationContext createXmlConfigApplicationContext(String xmlConfigPath, String...activeProfiles) {

    boolean classPath = xmlConfigPath.startsWith("classpath:");
    AbstractXmlApplicationContext applicationContext = classPath ?
      new ClassPathXmlApplicationContext() :
      new FileSystemXmlApplicationContext();

    applicationContext.setConfigLocation(xmlConfigPath);
    applicationContext.getEnvironment().setActiveProfiles(activeProfiles);
    applicationContext.refresh();
    return applicationContext;
  }
}
