package org.tiogasolutions.push.test;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.tiogasolutions.dev.jackson.TiogaJacksonTranslator;
import org.tiogasolutions.push.jackson.PushObjectMapper;
import org.tiogasolutions.push.kernel.config.CouchServersConfig;
import org.tiogasolutions.push.kernel.system.SessionStore;

import java.util.concurrent.TimeUnit;

import static org.tiogasolutions.dev.common.StringUtils.isNotBlank;

@Profile("test")
@Configuration
public class SpringTestConfig {

    @Bean
    public PushObjectMapper cpObjectMapper() {
        return new PushObjectMapper();
    }

    @Bean
    public TiogaJacksonTranslator tiogaJacksonTranslator(PushObjectMapper objectMapper) {
        return new TiogaJacksonTranslator(objectMapper);
    }

    @Bean
    public SessionStore sessionStore() {
        return new SessionStore(TimeUnit.MILLISECONDS.convert(1, TimeUnit.HOURS));
    }

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
}
