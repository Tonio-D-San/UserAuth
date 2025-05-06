package it.asansonne.authhub.util;

import java.util.Objects;
import org.hibernate.service.spi.ServiceException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

public class RestUtil {
  private static final RestTemplate restTemplate;
  private static final String HEADER_AUTHORIZATION = "Authorization";
  private static final String BEARER = "Bearer ";
  private RestUtil() { }

  static {
    restTemplate = new RestTemplate();
  }

  public static ResponseEntity<String> getResponseEntity(String url, HttpMethod httpMethod,
                                                         HttpEntity<String> entity) {
    try {
      Objects.requireNonNull(url, "rest.error.url.null");
      Objects.requireNonNull(httpMethod, "rest.error.http.method.null");
      Objects.requireNonNull(entity, "rest.error.entity.null");

      return restTemplate.exchange(url, httpMethod, entity, String.class);
    } catch (RestClientException e) {
      throw new ServiceException("rest.error.rest.call", e.getCause());
    }
  }

  public static HttpHeaders setHeader(JwtAuthenticationToken jwtAuthToken) {
    Objects.requireNonNull(jwtAuthToken, "rest.error.token.null");
    Objects.requireNonNull(jwtAuthToken.getToken(), "rest.error.token.null");

    HttpHeaders headers = new HttpHeaders();
    String tokenValue = jwtAuthToken.getToken().getTokenValue();

    if (StringUtils.hasText(tokenValue)) {
      headers.set(HEADER_AUTHORIZATION, BEARER + tokenValue);
      headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
    } else {
      throw new IllegalArgumentException("rest.error.token.value.invalid");
    }

    return headers;
  }
}
