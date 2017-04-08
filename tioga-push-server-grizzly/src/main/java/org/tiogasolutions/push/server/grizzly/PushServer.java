package org.tiogasolutions.push.server.grizzly;

import ch.qos.logback.classic.Level;
import org.slf4j.Logger;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.tiogasolutions.app.common.AppPathResolver;
import org.tiogasolutions.app.common.AppUtils;
import org.tiogasolutions.lib.spring.SpringUtils;
import org.tiogasolutions.notify.notifier.Notifier;
import org.tiogasolutions.runners.grizzly.GrizzlyServer;
import org.tiogasolutions.runners.grizzly.ShutdownUtils;

import java.nio.file.Path;

import static java.util.Arrays.asList;
import static org.slf4j.LoggerFactory.getLogger;

public class PushServer {

    private static final Logger log = getLogger(PushServer.class);

    public static void main(String... args) throws Exception {

        // Priority #1, configure default logging levels. This will be
        // overridden later when/if the logback.xml is found and loaded.
        AppUtils.initLogback(Level.WARN);

        // Assume we want by default INFO on when & how the grizzly server
        // is started. Possibly overwritten by logback.xml if used.
        AppUtils.setLogLevel(Level.INFO, PushServer.class);
        AppUtils.setLogLevel(Level.INFO, GrizzlyServer.class);

        // Load the resolver which gives us common tools for identifying the
        // runtime & config directories, logback.xml, etc.
        AppPathResolver resolver = new AppPathResolver("push_");
        Path runtimeDir = resolver.resolveRuntimePath();
        Path configDir = resolver.resolveConfigDir(runtimeDir);

        // Re-init logback if we can find the logback.xml
        Path logbackFile = AppUtils.initLogback(configDir, "push_log_config", "logback.xml");

        // Locate the spring file for this app or use DEFAULT_SPRING_FILE from the classpath if one is not found.
        String springConfigPath = resolver.resolveSpringPath(configDir, "classpath:/tioga-push-server-grizzly/spring-config.xml");
        String[] activeProfiles = resolver.resolveSpringProfiles(); // defaults to "hosted"

        boolean shuttingDown = asList(args).contains("-shutdown");
        String action = (shuttingDown ? "Shutting down" : "Starting");

        AbstractXmlApplicationContext applicationContext = SpringUtils.createXmlConfigApplicationContext(springConfigPath, activeProfiles);

        GrizzlyServer grizzlyServer = applicationContext.getBean(GrizzlyServer.class);

        Notifier notifier = applicationContext.getBean(Notifier.class);

        if (shuttingDown) {
            String msg = String.format("Shutting down push-server at %s:%s",
                    grizzlyServer.getConfig().getHostName(),
                    grizzlyServer.getConfig().getShutdownPort());

            log.warn(msg);
            notifier.begin().summary(msg).trait("action", "shutdown").send().get();

            ShutdownUtils.shutdownRemote(grizzlyServer.getConfig());
            System.exit(0);
            return;
        }

        String logMessage = String.format("%s solutions-server:\n" +
                        "  *  Runtime Dir     (solutions.runtime.dir)     %s\n" +
                        "  *  Config Dir      (solutions.config.dir)      %s\n" +
                        "  *  Logback File    (solutions.log.config)      %s\n" +
                        "  *  Spring Path     (solutions.spring.config)   %s\n" +
                        "  *  Active Profiles (solutions.active.profiles) %s",
                action, runtimeDir, configDir, logbackFile, springConfigPath, asList(activeProfiles));

        log.warn(logMessage);
        notifier.begin().summary(logMessage).trait("action", "startup").send().get();

        // Lastly, start the server.
        grizzlyServer.start();
    }
}
