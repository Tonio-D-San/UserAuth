package it.asansonne.authhub.ccsr.service;

import java.util.Map;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

public interface KeycloakService {

  HttpEntity<Map<String, Object>> updateUserAttribute(HttpHeaders headers, String status);

}
