package org.korhan.monithor;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.concurrent.Executor;

@SpringBootApplication
@EnableScheduling
@EnableAsync
@Slf4j
public class Monithor {

  public static final int CONNECTION_TIMEOUT = 30000;

  public static void main(String[] args) {
    SpringApplication.run(Monithor.class, args);
  }

  @Bean
  public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurer() {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedMethods("GET", "POST", "OPTIONS", "DELETE");
      }
    };
  }

  @Bean
  public HttpClient httpClient() {
    RequestConfig requestConfig = RequestConfig.custom()
      .setSocketTimeout(CONNECTION_TIMEOUT)
      .setConnectTimeout(CONNECTION_TIMEOUT)
      .setConnectionRequestTimeout(CONNECTION_TIMEOUT)
      .build();
    return HttpClients.custom()
      .setDefaultRequestConfig(requestConfig)
      .build();
  }

  @Bean
  public Executor executor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(10);
    executor.initialize();
    return executor;
  }
}
