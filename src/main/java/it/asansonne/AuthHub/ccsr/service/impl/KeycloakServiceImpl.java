package it.asansonne.authhub.ccsr.service.impl;

import static it.asansonne.authhub.Attributes.PENDING;
import static it.asansonne.authhub.util.RestUtil.getResponseEntity;
import static it.asansonne.authhub.util.RestUtil.setHeader;

import it.asansonne.authhub.ccsr.service.KeycloakService;
import it.asansonne.authhub.dto.response.UserResponse;
import it.asansonne.authhub.mapper.impl.UserModelMapper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KeycloakServiceImpl implements KeycloakService {
  @Value("${keycloak.host.user}")
  private String urlUser;
  private final UserModelMapper userModelMapper;

  @Override
  public HttpEntity<Map<String, Object>> updateUserAttribute(HttpHeaders headers, String status) {
    Map<String, Object> payload = new HashMap<>();
    Map<String, List<String>> attributes = new HashMap<>();
    attributes.put("registration.status.attribute", List.of(status));
    payload.put("attributes", attributes);
    return new HttpEntity<>(payload, headers);
  }

  @Scheduled(fixedDelay = 60000)
  public void checkIncompleteUsers() {
    try {
      if (SecurityContextHolder.getContext().getAuthentication()
          instanceof JwtAuthenticationToken jwtAuthToken) {
        ResponseEntity<String> response = getResponseEntity(
            urlUser,
            HttpMethod.GET,
            new HttpEntity<>("{}", setHeader(jwtAuthToken))
        );
        if (response.getBody() == null) {
          log.error("Response non valida durante il controllo degli utenti incompleti");
          return;
        }

        List<UserResponse> users = userModelMapper.jsonToDto(response.getBody());
        users.stream()
            .filter(this::isPendingUser)
            .forEach(user ->
                log.warn("Utente incompleto trovato - Username: {}, Email: {}",
                    user.getUsername(), user.getEmail())
            );
      }
    } catch (Exception e) {
      log.error("Errore durante il controllo degli utenti incompleti", e);
    }
  }

  private boolean isPendingUser(UserResponse user) {
    if (user == null) return false;
    Map<String, List<String>> attrs = user.getAttributes();
    if (attrs == null || !attrs.containsKey("registration_status")) return false;
    List<String> statusList = attrs.get("registration_status");
    return statusList != null && !statusList.isEmpty()
        && PENDING.getValue().equals(statusList.getFirst());
  }
}
