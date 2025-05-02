package it.asansonne.authhub.ccsr.controller.impl;

import static it.asansonne.authhub.constant.SharedConstant.ADMIN_ROLES;
import static it.asansonne.authhub.constant.SharedConstant.ADMIN_USER_ROLES;
import static it.asansonne.authhub.constant.SharedConstant.API;
import static it.asansonne.authhub.constant.SharedConstant.API_VERSION;
import static it.asansonne.authhub.constant.SharedConstant.USER_ROLES;

import io.swagger.v3.oas.annotations.tags.Tag;
import it.asansonne.authhub.ccsr.component.UserComponent;
import it.asansonne.authhub.ccsr.controller.UserControllerMappingV1;
import it.asansonne.authhub.dto.request.UserGroupRequest;
import it.asansonne.authhub.dto.request.UserRequest;
import it.asansonne.authhub.dto.request.UserUpdateRequest;
import it.asansonne.authhub.dto.request.StatusRequest;
import it.asansonne.authhub.dto.response.UserResponse;
import java.security.Principal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * The type User controller.
 */
@RestController
@RequestMapping(API + "/" + API_VERSION + "/users")
@AllArgsConstructor
@Tag(name = "UserController" + API_VERSION)
@PreAuthorize(ADMIN_ROLES)
public class UserControllerV1Impl implements UserControllerMappingV1 {
  private final UserComponent userComponent;

  @Override
  public UserResponse findUserByUuid(UUID uuid) {
    return userComponent.findUserByUuid(uuid);
  }

  @Override
  public Page<UserResponse> findAllUsers(Integer page, Integer size, String direction) {
    return userComponent.findAllUsers(
        PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(direction), SURNAME))
    );
  }

  @Override
  @PreAuthorize(ADMIN_USER_ROLES)
  public Page<UserResponse> findActiveUsers(Integer page, Integer size, String direction) {
    return userComponent.findActiveUsers(
        PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(direction), SURNAME))
    );
  }

  @Override
  public Page<UserResponse> findInactiveUsers(Integer page, Integer size, String direction) {
    return userComponent.findInactiveUsers(
        PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(direction), SURNAME))
    );
  }

  @Override
  public ResponseEntity<UserResponse> createUser(UserRequest userRequest,
                                                 UriComponentsBuilder builder) {
    UserResponse response = userComponent.createUser(userRequest);
    return ResponseEntity
        .created(builder
            .path(API + "/" + API_VERSION + "/admin/")
            .buildAndExpand(response)
            .toUri())
        .body(response);
  }

  @Override
  @PreAuthorize(USER_ROLES)
  public UserResponse updateUserByUuid(Principal principal, UserUpdateRequest userRequest,
                                       UUID uuid) {
    return userComponent.updateUserByUuid(principal, userRequest, uuid);
  }

  @Override
  @PreAuthorize(USER_ROLES)
  public UserResponse updateMe(Principal principal, UserUpdateRequest userRequest) {
    return null;
  }

  @Override
  public UserResponse updateGroupByUserUuid(UserGroupRequest userRequest, UUID uuid) {
    return userComponent.updateGroupByUserUuid(userRequest, uuid);
  }

  @Override
  public void updateStatusUserByUuid(UUID uuid, StatusRequest status) {
    userComponent.updateStatusUserByUuid(uuid, status);
  }
}
