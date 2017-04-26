package org.tiogasolutions.push.server.grizzly;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.spring.scope.RequestContextFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.tiogasolutions.dev.jackson.TiogaJacksonTranslator;
import org.tiogasolutions.notify.notifier.Notifier;
import org.tiogasolutions.notify.notifier.send.LoggingNotificationSender;
import org.tiogasolutions.notify.notifier.send.NotificationSender;
import org.tiogasolutions.notify.sender.couch.CouchNotificationSender;
import org.tiogasolutions.push.engine.system.PushApplication;
import org.tiogasolutions.push.jackson.PushObjectMapper;
import org.tiogasolutions.push.kernel.config.CouchServersConfig;
import org.tiogasolutions.push.kernel.config.SystemConfiguration;
import org.tiogasolutions.push.kernel.execution.ExecutionManager;
import org.tiogasolutions.push.kernel.requests.PushRequestStore;
import org.tiogasolutions.push.kernel.system.PluginManager;
import org.tiogasolutions.push.kernel.system.PushCouchServer;
import org.tiogasolutions.push.kernel.system.SessionStore;
import org.tiogasolutions.push.plugins.ses.SesEmailPlugin;
import org.tiogasolutions.push.plugins.smtp.SmtpEmailPlugin;
import org.tiogasolutions.push.plugins.twilio.TwilioPlugin;
import org.tiogasolutions.push.plugins.xmpp.XmppPlugin;
import org.tiogasolutions.runners.grizzly.GrizzlyServer;
import org.tiogasolutions.runners.grizzly.GrizzlyServerConfig;

import java.net.UnknownHostException;
import java.util.Arrays;

@Profile("hosted")
@Configuration
public class PushHostedSpringConfig {

    @Bean(name="org.tiogasolutions.push.kernel.config.SystemConfiguration")
    public SystemConfiguration systemConfiguration() {
      return new SystemConfiguration("*");
    }

    @Bean
    public PushObjectMapper pushObjectMapper() {
        return new PushObjectMapper();
    }

    @Bean
    public TiogaJacksonTranslator tiogaJacksonTranslator(PushObjectMapper pushObjectMapper) {
        return new TiogaJacksonTranslator(pushObjectMapper);
    }

    @Bean
    public SessionStore sessionStore(@Value("${push_sessionDuration}") int sessionDuration) {
        return new SessionStore(sessionDuration);
    }

    @Bean
    public PushCouchServer pushCouchServer(CouchServersConfig config) {
        return new PushCouchServer(config);
    }

    @Bean
    public ExecutionManager executionManager() {
        return new ExecutionManager();
    }

    @Bean
    public PushRequestStore pushRequestStore(ExecutionManager executionManager) {
        return new PushRequestStore(executionManager);
    }

    @Bean
    public PluginManager pluginManager(ExecutionManager executionManager, PushRequestStore pushRequestStore, PushObjectMapper pushObjectMapper) {
        return new PluginManager(Arrays.asList(
                new XmppPlugin(executionManager, pushObjectMapper, pushRequestStore),
                new SesEmailPlugin(executionManager, pushObjectMapper, pushRequestStore),
                new SmtpEmailPlugin(executionManager, pushObjectMapper, pushRequestStore),
                new TwilioPlugin(executionManager, pushObjectMapper, pushRequestStore)
        ));
    }

    @Bean
    public GrizzlyServerConfig grizzlyServerConfig(@Value("${push_hostName}") String hostName,
                                                   @Value("${push_port}") int port,
                                                   @Value("${push_shutdownPort}") int shutdownPort,
                                                   @Value("${push_context}") String context,
                                                   @Value("${push_toOpenBrowser}") boolean toOpenBrowser) {

        GrizzlyServerConfig config = new GrizzlyServerConfig();
        config.setHostName(hostName);
        config.setPort(port);
        config.setShutdownPort(shutdownPort);
        config.setContext(context);
        config.setToOpenBrowser(toOpenBrowser);

        return config;
    }

    @Bean
    public CouchServersConfig couchServersConfig(@Value("${push_masterUrl}") String masterUrl,
                                                 @Value("${push_masterUsername}") String masterUsername,
                                                 @Value("${push_masterPassword}") String masterPassword,
                                                 @Value("${push_masterDatabaseName}") String masterDatabaseName,

                                                 @Value("${push_domainUrl}") String domainUrl,
                                                 @Value("${push_domainUsername}") String domainUsername,
                                                 @Value("${push_domainPassword}") String domainPassword,
                                                 @Value("${push_domainDatabasePrefix}") String domainDatabasePrefix) {

        CouchServersConfig config = new CouchServersConfig();

        config.setMasterUrl(masterUrl);
        config.setMasterUsername(masterUsername);
        config.setMasterPassword(masterPassword);
        config.setMasterDatabaseName(masterDatabaseName);

        config.setDomainUrl(domainUrl);
        config.setDomainUsername(domainUsername);
        config.setDomainPassword(domainPassword);
        config.setDomainDatabasePrefix(domainDatabasePrefix);

        return config;
    }

    @Bean
    public GrizzlyServer grizzlyServer(GrizzlyServerConfig grizzlyServerConfig, PushApplication application, ApplicationContext applicationContext) {

        ResourceConfig resourceConfig = ResourceConfig.forApplication(application);
        resourceConfig.property("contextConfig", applicationContext);
        resourceConfig.packages("org.tiogasolutions.push");
        resourceConfig.register(RequestContextFilter.class, 1);

        return new GrizzlyServer(grizzlyServerConfig, resourceConfig);
    }

    @Bean
    public Notifier notifier(@Value("${notifier_couch_url}") String couchUrl,
                             @Value("${notifier_couch_database_name}") String databaseName,
                             @Value("${notifier_couch_username}") String username,
                             @Value("${notifier_couch_password}") String password,
                             @Value("${notifier_force_logger}") boolean forceLogger) {

        NotificationSender sender = forceLogger ?
                new LoggingNotificationSender() :
                new CouchNotificationSender(couchUrl, databaseName, username,  password);

        return new Notifier(sender).onBegin(builder -> {
            builder.topic("Push Engine");

            try {
                String hostname = java.net.InetAddress.getLocalHost().getHostName();
                builder.trait("source", System.getProperty("user.name") + "@" + hostname);

            } catch (UnknownHostException ignored) {
                builder.trait("source", System.getProperty("user.name") + "@" + "UNKNOWN");
            }
        });
    }
}
