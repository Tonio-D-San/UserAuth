package it.asansonne.authhub.ccsr.component.impl;

import static it.asansonne.authhub.constant.SharedConstant.USER_GROUP;
import static it.asansonne.authhub.enums.MessageConstant.GROUP_NOT_FOUND;
import static it.asansonne.authhub.enums.MessageConstant.JWT_ERROR;
import static it.asansonne.authhub.enums.MessageConstant.PERSON_NOT_FOUND;
import static it.asansonne.authhub.util.RestUtil.getResponseEntity;
import static it.asansonne.authhub.util.RestUtil.setHeader;

import it.asansonne.authhub.ccsr.component.KeycloakComponent;
import it.asansonne.authhub.ccsr.service.GroupService;
import it.asansonne.authhub.dto.request.UserRequest;
import it.asansonne.authhub.dto.request.StatusRequest;
import it.asansonne.authhub.dto.response.UserResponse;
import it.asansonne.authhub.exception.custom.NotFoundException;
import it.asansonne.authhub.mapper.ResponseModelMapper;
import it.asansonne.authhub.mapper.impl.GroupModelMapper;
import it.asansonne.authhub.mapper.impl.UserModelMapper;
import it.asansonne.authhub.model.jpa.GroupJpa;
import it.asansonne.authhub.model.jpa.UserJpa;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

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
  private final GroupModelMapper groupModelMapper;
  @Value("${keycloak.host.user}")
  private String urlUser;
  @Value("${keycloak.host.clients}")
  private String urlClient;
  @Value("${keycloak.host.groups}")
  private String urlGroup;

  /**
   * Read user from Keycloak by username.
   *
   * @param email of the user
   */
  public UserJpa readUser(String email) {
    if (SecurityContextHolder.getContext().getAuthentication()
        instanceof JwtAuthenticationToken jwtAuthToken) {
      ResponseEntity<String> response = getUserByEmail(email, jwtAuthToken);
      validateResponse(response);
      return responseModelMapper.dtoToModelResponse(
          userMapper.jsonToDto(response.getBody()).getFirst()
      );
    }
    throw new IllegalStateException(JWT_ERROR.getMessage());
  }

  /**
   * Read user from Keycloak by userId.
   *
   * @param userId of the user
   */
  @Override
  public UserJpa readMe(UUID userId) {
    if (SecurityContextHolder.getContext().getAuthentication()
        instanceof JwtAuthenticationToken jwtAuthToken) {
      ResponseEntity<String> response = getMyProfile(userId, jwtAuthToken);
      validateResponse(response);
      GroupJpa userGroup = addUserGroup(userId, jwtAuthToken);
      removeRole(userId, jwtAuthToken);
      return responseModelMapper.dtoToModelResponse(
          userMapper.myJsonToDto(response.getBody(), userGroup) //TODO: mappare tutto e salvare
      );
    }
    throw new IllegalStateException(JWT_ERROR.getMessage());
  }

  private GroupJpa addUserGroup(UUID userId, JwtAuthenticationToken jwtAuthToken) {
    GroupJpa group = groupService.findGroupByUuid(USER_GROUP)
        .orElseThrow(() -> new NotFoundException(GROUP_NOT_FOUND.getMessage()));
    updateGroup(userId, group, jwtAuthToken);
    return group;
  }

  private ResponseEntity<String> getMyProfile(UUID userId, JwtAuthenticationToken jwtAuthToken) {
    return getResponseEntity(
        String.format("%s/%s", urlUser, userId),
        HttpMethod.GET,
        new HttpEntity<>(setHeader(jwtAuthToken))
    );
  }

  /**
   * Create a user keycloak response.
   *
   * @param request of the user
   */
  public void createUser(UserRequest request) {
    if (SecurityContextHolder.getContext().getAuthentication()
        instanceof JwtAuthenticationToken jwtAuthToken) {
      getResponseEntity(
          urlUser,
          HttpMethod.POST,
          new HttpEntity<>(groupModelMapper.setPayload(request), setHeader(jwtAuthToken))
      );
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
      updateGroup(userUuid, request, jwtAuthToken);
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
      getResponseEntity(
          urlUser + "/" + userUuid.toString(),
          HttpMethod.PUT,
          new HttpEntity<>(groupModelMapper.setPayload(status), setHeader(jwtAuthToken))
      );
    } else {
      throw new IllegalStateException(JWT_ERROR.getMessage());
    }
  }

  @Override
  public void deleteUserGroup(UUID userUuid, GroupJpa group) {
    if (SecurityContextHolder.getContext().getAuthentication()
        instanceof JwtAuthenticationToken jwtAuthToken) {
      getResponseEntity(
          urlUser + "/" + userUuid.toString() + "/groups/" + group.getUuid(),
          HttpMethod.DELETE,
          new HttpEntity<>("{}", setHeader(jwtAuthToken))
      );
    } else {
      throw new IllegalStateException(JWT_ERROR.getMessage());
    }
  }

  private ResponseEntity<String> getUserByEmail(String email, JwtAuthenticationToken jwtAuthToken) {
    return getResponseEntity(
        urlUser + "?email=" + email,
        HttpMethod.GET,
        new HttpEntity<>(setHeader(jwtAuthToken))
    );
  }

  private void validateResponse(ResponseEntity<String> response) {
    if (response.getStatusCode() != HttpStatus.OK) {
      throw new NotFoundException(PERSON_NOT_FOUND.getMessage());
    }
  }

  private void updateGroup(UUID userUuid, GroupJpa groupId, JwtAuthenticationToken jwtAuthToken) {
    getResponseEntity(
        String.format("%s/%s/groups/%s", urlUser, userUuid, groupId.getUuid()),
        HttpMethod.PUT,
        new HttpEntity<>("{}", setHeader(jwtAuthToken))
    );
  }

  private void getGroups(JwtAuthenticationToken jwtAuthToken) {
    //GET /admin/realms/{realm}/groups
    getResponseEntity(
        urlGroup,
        HttpMethod.GET,
        new HttpEntity<>("{}", setHeader(jwtAuthToken))
    );
  }

  private void removeRole(UUID userUuid, JwtAuthenticationToken jwtAuthToken) {
    getResponseEntity(
        String.format("%s/%s/role-mappings/clients/%s",
            urlUser, userUuid, groupService.fetchAllGroups(jwtAuthToken).getFirst().getUuid()),
        HttpMethod.DELETE,
        new HttpEntity<>("{}", setHeader(jwtAuthToken))
    );
  }
}
