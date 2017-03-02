package org.tiogasolutions.push.plugins.xmpp;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.tiogasolutions.push.jackson.PushObjectMapper;
import org.tiogasolutions.push.kernel.config.CouchServersConfig;
import org.tiogasolutions.push.kernel.execution.ExecutionManager;
import org.tiogasolutions.push.kernel.requests.PushRequestStore;
import org.tiogasolutions.push.kernel.system.PluginManager;

import static java.util.Collections.singletonList;
import static org.tiogasolutions.dev.common.StringUtils.isNotBlank;

@Profile("test")
@Configuration
public class XmppSpringTestConfig {

    @Bean
    public CouchServersConfig couchServersConfig() {
        CouchServersConfig config = new CouchServersConfig();

        String couchUrl = "http://127.0.0.1:5984";
        String username = "test-user";
        String password = "test-user";

        if (isNotBlank(System.getenv("awsCouchUrl"))) {
            couchUrl = System.getenv("awsCouchUrl");
        }
        if (isNotBlank(System.getenv("awsCouchUsername"))) {
            username = System.getenv("awsCouchUsername");
        }
        if (isNotBlank(System.getenv("awsCouchPassword"))) {
            password = System.getenv("awsCouchPassword");
        }

        config.setMasterUrl(couchUrl);
        config.setMasterUsername(username);
        config.setMasterPassword(password);
        config.setMasterDatabaseName("test-push");

        config.setDomainUrl(couchUrl);
        config.setDomainUsername(username);
        config.setDomainPassword(password);
        config.setDomainDatabasePrefix("test-push-");

        return config;
    }

    @Bean
    public PluginManager pluginManager(ExecutionManager executionManager, PushObjectMapper objectMapper, PushRequestStore pushRequestStore) {
        return new PluginManager(singletonList(new XmppPlugin(executionManager, objectMapper, pushRequestStore)));
    }
}
