package it.asansonne.userauth.ccsr.component.impl;

import static it.asansonne.userauth.enums.MessageConstant.GROUP_NOT_FOUND;
import static it.asansonne.userauth.enums.MessageConstant.JWT_ERROR;
import static it.asansonne.userauth.enums.MessageConstant.PERSON_NOT_FOUND;

import it.asansonne.userauth.ccsr.component.KeycloakComponent;
import it.asansonne.userauth.ccsr.service.GroupService;
import it.asansonne.userauth.dto.request.GroupRequest;
import it.asansonne.userauth.dto.request.UserRequest;
import it.asansonne.userauth.dto.request.StatusRequest;
import it.asansonne.userauth.dto.response.UserResponse;
import it.asansonne.userauth.exception.custom.NotFoundException;
import it.asansonne.userauth.mapper.ResponseModelMapper;
import it.asansonne.userauth.mapper.impl.UserModelMapper;
import it.asansonne.userauth.model.jpa.GroupJpa;
import it.asansonne.userauth.model.jpa.UserJpa;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * The type Keycloak component.
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class KeycloakComponentImpl implements KeycloakComponent {
  private final UserModelMapper userMapper;
  private final ResponseModelMapper<UserJpa, UserResponse> responseModelMapper;
  private final GroupService groupService;
  @Value("${keycloak.host.user}")
  private String urlUser;

  /**
   * Read user from Keycloak by username.
   *
   * @param email of the user
   */
  public UserJpa readUser(String email) {
    if (SecurityContextHolder.getContext().getAuthentication()
        instanceof JwtAuthenticationToken jwtAuthToken) {
      ResponseEntity<String> response = new RestTemplate()
          .exchange(urlUser + "?email=" + email, HttpMethod.GET,
              new HttpEntity<>(setHeader(jwtAuthToken)), String.class);
      if (response.getStatusCode() != HttpStatus.OK) {
        throw new NotFoundException(PERSON_NOT_FOUND.getMessage());
      }
      return responseModelMapper.dtoToModelResponse(
          userMapper.jsonToDto(response.getBody()).getFirst()
      );
    }

    throw new IllegalStateException(JWT_ERROR.getMessage());
  }

  /**
   * Create a user keycloak response.
   *
   * @param request of the user
   */
  public void createUser(UserRequest request) {
    if (SecurityContextHolder.getContext().getAuthentication()
        instanceof JwtAuthenticationToken jwtAuthToken) {
      new RestTemplate()
          .exchange(urlUser, HttpMethod.POST,
              new HttpEntity<>(setPayload(request), setHeader(jwtAuthToken)), String.class);
    } else {
      throw new IllegalStateException(JWT_ERROR.getMessage());
    }
  }

  /**
   * Update user in Keycloak.
   *
   * @param userUuid the user id
   * @param request  updated user data
   */
  public void updateUser(UUID userUuid, GroupJpa request) {
    if (SecurityContextHolder.getContext().getAuthentication()
        instanceof JwtAuthenticationToken jwtAuthToken) {
      new RestTemplate()
          .exchange(urlUser + "/" + userUuid.toString()
                  + "/groups/"
                  + request.getUuid(), HttpMethod.PUT,
              new HttpEntity<>("{}", setHeader(jwtAuthToken)), String.class);
    } else {
      throw new IllegalStateException(JWT_ERROR.getMessage());
    }
  }

  /**
   * Update the status user in Keycloak.
   *
   * @param userUuid the user id
   * @param status   updated user status
   */
  public void updateStatusUser(UUID userUuid, StatusRequest status) {
    if (SecurityContextHolder.getContext().getAuthentication()
        instanceof JwtAuthenticationToken jwtAuthToken) {
      new RestTemplate()
          .exchange(urlUser + "/" + userUuid.toString(), HttpMethod.PUT,
              new HttpEntity<>(setPayload(status), setHeader(jwtAuthToken)), String.class);
    } else {
      throw new IllegalStateException(JWT_ERROR.getMessage());
    }
  }

  @Override
  public void deleteUserGroup(UUID userUuid, GroupJpa group) {
    if (SecurityContextHolder.getContext().getAuthentication()
        instanceof JwtAuthenticationToken jwtAuthToken) {
      new RestTemplate()
          .exchange(urlUser + "/" + userUuid.toString() + "/groups/"
                  + group.getUuid(), HttpMethod.DELETE,
              new HttpEntity<>("{}", setHeader(jwtAuthToken)), String.class);
    } else {
      throw new IllegalStateException(JWT_ERROR.getMessage());
    }
  }

  private HttpHeaders setHeader(JwtAuthenticationToken jwtAuthToken) {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Bearer " + jwtAuthToken.getToken().getTokenValue());
    headers.set("Content-Type", "application/json");
    return headers;
  }

  private String setPayload(UserRequest request) {
    if (request.getGroups() == null || request.getGroups().isEmpty()) {
      return "{"
          + "\"username\": \"" + request.getUsername() + "\","
          + "\"credentials\": [{"
          + "    \"type\": \"password\","
          + "    \"value\": \"" + request.getPassword() + "\","
          + "    \"temporary\": true"
          + "}],"
          + "\"requiredActions\": [\"UPDATE_PASSWORD\"],"
          + "\"email\": \"" + request.getEmail() + "\","
          + "\"lastName\": \"" + request.getLastname() + "\","
          + "\"firstName\": \"" + request.getFirstname() + "\","
          + "\"enabled\": " + request.getIsActive()
          + "}";
    } else {
      return "{"
          + "\"username\": \"" + request.getUsername() + "\","
          + "\"credentials\": [{"
          + "    \"type\": \"password\","
          + "    \"value\": \"" + request.getPassword() + "\","
          + "    \"temporary\": true"
          + "}],"
          + "\"requiredActions\": [\"UPDATE_PASSWORD\"],"
          + "\"email\": \"" + request.getEmail() + "\","
          + "\"lastName\": \"" + request.getLastname() + "\","
          + "\"firstName\": \"" + request.getFirstname() + "\","
          + "\"groups\": " + groupJson(request.getGroups()) + ","
          + "\"enabled\": " + request.getIsActive()
          + "}";
    }
  }

  private String setPayload(StatusRequest status) {
    return "{"
        + "\"enabled\": " + status.getIsActive()
        + "}";
  }

  private StringBuilder groupJson(List<GroupRequest> groups) {
    StringBuilder groupsJson = new StringBuilder("[");
    for (GroupRequest group : groups) {
      if (groupsJson.length() > 1) {
        groupsJson.append(",");
      }
      groupsJson.append("\"")
          .append(groupService.findGroupByUuid(group.getUuid())
              .orElseThrow(() -> new NotFoundException(GROUP_NOT_FOUND.getMessage()))
              .getPath())
          .append("\"");
    }
    groupsJson.append("]");
    return groupsJson;
  }
}
