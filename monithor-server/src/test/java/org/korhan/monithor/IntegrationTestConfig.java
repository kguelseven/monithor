package org.korhan.monithor;

import org.apache.hc.client5.http.classic.HttpClient;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = {"org.korhan.monithor.service", "org.korhan.monithor.check", "org.korhan.monithor.data.persistence"})
@EnableAutoConfiguration
public class IntegrationTestConfig {
  // separate Config here since we don't want Scheduled-Jobs to run during tests

  @Bean
  IntegrationTestUtils restUtils() {
    return new IntegrationTestUtils();
  }

  @Bean
  public HttpClient httpClient() {
     return new Monithor().httpClient();
  }
}
