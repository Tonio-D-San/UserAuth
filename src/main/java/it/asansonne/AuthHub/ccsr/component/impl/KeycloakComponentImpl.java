package it.asansonne.authhub.ccsr.component.impl;

import static it.asansonne.authhub.constant.SharedConstant.FIRST_ACCESS_GROUP;
import static it.asansonne.authhub.constant.SharedConstant.USER_GROUP;
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
    throw new IllegalStateException("jwt.error");
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
      deleteUserGroup(userId, addUserGroup(userId, FIRST_ACCESS_GROUP, jwtAuthToken));
      return responseModelMapper.dtoToModelResponse(
          userMapper.myJsonToDto(
              response.getBody(),
              addUserGroup(userId, USER_GROUP, jwtAuthToken)
          ) //TODO: mappare tutto e salvare
      );
    }
    throw new IllegalStateException("jwt.error");
  }

  /**
   * Create a user keycloak response.
   *
   * @param request of the user
   */
  @Override
  public void createUser(UserRequest request) {
    if (SecurityContextHolder.getContext().getAuthentication()
        instanceof JwtAuthenticationToken jwtAuthToken) {
      getResponseEntity(
          urlUser,
          HttpMethod.POST,
          new HttpEntity<>(groupModelMapper.setPayload(request), setHeader(jwtAuthToken))
      );
    } else {
      throw new IllegalStateException("jwt.error");
    }
  }

  /**
   * Update user in Keycloak.
   *
   * @param userUuid the user id
   * @param request  updated user data
   */
  @Override
  public void updateUser(UUID userUuid, GroupJpa request) {
    if (SecurityContextHolder.getContext().getAuthentication()
        instanceof JwtAuthenticationToken jwtAuthToken) {
      updateGroup(userUuid, request.getUuid(), jwtAuthToken);
    } else {
      throw new IllegalStateException("jwt.error");
    }
  }

  /**
   * Update the status user in Keycloak.
   *
   * @param userUuid the user id
   * @param status   updated user status
   */
  @Override
  public void updateStatusUser(UUID userUuid, StatusRequest status) {
    if (SecurityContextHolder.getContext().getAuthentication()
        instanceof JwtAuthenticationToken jwtAuthToken) {
      getResponseEntity(
          urlUser + "/" + userUuid.toString(),
          HttpMethod.PUT,
          new HttpEntity<>(groupModelMapper.setPayload(status), setHeader(jwtAuthToken))
      );
    } else {
      throw new IllegalStateException("jwt.error");
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
      throw new IllegalStateException("jwt.error");
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
      throw new NotFoundException("person.not.found");
    }
  }

  /**
   * Add a group by UUID
   * @param userId the user id
   * @param jwtAuthToken the jwt auth token
   * @return the group
   */
  private GroupJpa addUserGroup(UUID userId, UUID groupId, JwtAuthenticationToken jwtAuthToken) {
    GroupJpa group = groupService.findGroupByUuid(groupId)
        .orElseThrow(() -> new NotFoundException("group.not.found"));
    updateGroup(userId, group.getUuid(), jwtAuthToken);
    return group;
  }

  /**
   * GET .../admin/realms/{realm-name}/users/{user-id}
   *
   * @param userId the user id
   * @param jwtAuthToken the jwt auth token
   * @return the response entity
   */
  private ResponseEntity<String> getMyProfile(UUID userId, JwtAuthenticationToken jwtAuthToken) {
    return getResponseEntity(
        String.format("%s/%s", urlUser, userId),
        HttpMethod.GET,
        new HttpEntity<>(setHeader(jwtAuthToken))
    );
  }

  /**
   * GET .../admin/realms/${realm-name}/users/{user-id}/groups/{group-id}
   * @param userUuid the user id
   * @param groupUUid the group id
   * @param jwtAuthToken the jwt auth token
   */
  private void updateGroup(UUID userUuid, UUID groupUUid, JwtAuthenticationToken jwtAuthToken) {
    getResponseEntity(
        String.format("%s/%s/groups/%s", urlUser, userUuid, groupUUid),
        HttpMethod.PUT,
        new HttpEntity<>("{}", setHeader(jwtAuthToken))
    );
  }

}
