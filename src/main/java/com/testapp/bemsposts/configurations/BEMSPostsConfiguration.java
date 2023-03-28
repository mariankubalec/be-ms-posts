package com.testapp.bemsposts.configurations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class BEMSPostsConfiguration {
  @Value("#{environment.APIURL}")
  private String apiURL;

  @Bean
  public WebClient defaultWebClient() {
    return WebClient.builder()
        .baseUrl(apiURL)
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build();
  }
}
