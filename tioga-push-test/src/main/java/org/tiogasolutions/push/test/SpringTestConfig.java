package org.tiogasolutions.push.test;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.tiogasolutions.apis.bitly.BitlyApis;
import org.tiogasolutions.dev.common.EnvUtils;
import org.tiogasolutions.dev.jackson.TiogaJacksonTranslator;
import org.tiogasolutions.push.jackson.PushObjectMapper;
import org.tiogasolutions.push.kernel.config.CouchServersConfig;
import org.tiogasolutions.push.kernel.system.SessionStore;

import java.util.concurrent.TimeUnit;

@Profile("test")
@Configuration
public class SpringTestConfig {

  private String getBitlyAccessToken() {
    return EnvUtils.findProperty("tioga.test.bitly.access.token", "no-such-luck");
  }

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
  public BitlyApis bitlyApis() {
    return new BitlyApis(getBitlyAccessToken());
  }

  @Bean
  public CouchServersConfig couchServersConfig() {
    CouchServersConfig config = new CouchServersConfig();

    config.setMasterUrl("http://localhost:5984");
    config.setMasterUsername("test-user");
    config.setMasterPassword("test-user");
    config.setMasterDatabaseName("test-push");

    config.setDomainUrl("http://localhost:5984");
    config.setDomainUserName("test-user");
    config.setDomainPassword("test-user");
    config.setDomainDatabasePrefix("test-push-");

    return config;
  }
}
