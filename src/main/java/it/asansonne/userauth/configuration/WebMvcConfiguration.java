package it.asansonne.userauth.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * The type Web mvc configuration.
 */
@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {
  @Value("${server.cors.allowed.methods:GET,POST,PUT,DELETE}")
  private String[] allowedMethods;
  @Value("${server.cors.allowed.origins:*}")
  private String[] allowedOrigins;
  @Value("${server.cors.allowed.headers:*}")
  private String[] allowedHeaders;

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry
        .addMapping("/api/v*/**")
        .allowedOrigins(allowedOrigins)
        .allowedMethods(allowedMethods)
        .allowedHeaders(allowedHeaders)
        .allowedOriginPatterns();
  }
}
