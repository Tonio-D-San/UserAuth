package it.asansonne.authhub.ccsr.component.impl;

import static it.asansonne.authhub.constant.SharedConstant.BASE_GROUP;

import it.asansonne.authhub.ccsr.component.KeycloakComponent;
import it.asansonne.authhub.ccsr.component.UserComponent;
import it.asansonne.authhub.ccsr.service.GroupService;
import it.asansonne.authhub.ccsr.service.UserService;
import it.asansonne.authhub.dto.request.GroupRequest;
import it.asansonne.authhub.dto.request.UserGroupRequest;
import it.asansonne.authhub.dto.request.UserRequest;
import it.asansonne.authhub.dto.request.UserUpdateRequest;
import it.asansonne.authhub.dto.request.StatusRequest;
import it.asansonne.authhub.dto.response.UserResponse;
import it.asansonne.authhub.exception.custom.NotFoundException;
import it.asansonne.authhub.mapper.ResponseModelMapper;
import it.asansonne.authhub.mapper.impl.GroupModelMapper;
import it.asansonne.authhub.model.jpa.GroupJpa;
import it.asansonne.authhub.model.jpa.UserJpa;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

/**
 * The type User component.
 */
@Component
@AllArgsConstructor
public class UserComponentImpl implements UserComponent {
  private final KeycloakComponent keycloakComponent;
  private final UserService userService;
  private final GroupService groupService;
  private final ResponseModelMapper<UserJpa, UserResponse> userResponseModelMapper;
  private final GroupModelMapper groupMapper;

  @Override
  public UserResponse findUserByUuid(UUID userUuid) {
    UserJpa user = findUser(userUuid);
    UserJpa userResponse = keycloakComponent.readUser(user.getEmail());
    userResponse.setGroups(user.getGroups());
    userResponse.setBiography(user.getBiography());
    userResponse.setProfileImage(user.getProfileImage());
    return userResponseModelMapper.toDto(userResponse);
  }

  @Override
  public Page<UserResponse> findAllUsers(Pageable pageable) {
    return userResponseModelMapper.toDto(userService.findAllUsers(pageable), pageable);
  }

  @Override
  public Page<UserResponse> findActiveUsers(Pageable pageable) {
    return userResponseModelMapper.toDto(userService.findActiveUsers(pageable), pageable);
  }

  @Override
  public Page<UserResponse> findInactiveUsers(Pageable pageable) {
    return userResponseModelMapper.toDto(userService.findInactiveUsers(pageable), pageable);
  }

  @Override
  public UserResponse createUser(UserRequest userRequest) {
    if (userRequest.getGroups() == null || userRequest.getGroups().isEmpty()) {
      userRequest.setGroups(List.of(groupToGroupRequest()));
    }
    keycloakComponent.createUser(userRequest);
    UserJpa user = keycloakComponent.readUser(userRequest.getEmail());
    List<GroupJpa> groups = listGroups(userRequest.getGroups());
    user.setGroups(groups);
    for (GroupJpa group : groups) {
      group.getUsers().add(user);
    }
    user.setBiography(userRequest.getBiography());
    user.setProfileImage(userRequest.getProfileImage());
    return userResponseModelMapper.toDto(userService.updateUser(user));
  }

  @Override
  public UserResponse updateUserByUuid(
      Principal principal, UserUpdateRequest userUpdateRequest, UUID userUuid
  ) {
    if (findUser(UUID.fromString(principal.getName().split("[,\\[\\]\\s]+")[1])).getUuid()
        .equals(userUuid)) {
      return userResponseModelMapper.toDto(
          userService.updateUser(updateFields(userUpdateRequest, findUser(userUuid)))
      );
    } else {
      throw new AccessDeniedException("forbidden");
    }
  }

  @Override
  public UserResponse updateMe(Principal principal, UserUpdateRequest userUpdateRequest) {
    return userResponseModelMapper.toDto(
      userService.updateUser(
        keycloakComponent.readMe(
          UUID.fromString(principal.getName().split("[,\\[\\]\\s]+")[1]),
          userUpdateRequest
        )
      )
    );
  }

  // userService.updateGroupsUser is a method that saves the user
  @Override
  public UserResponse updateGroupByUserUuid(UserGroupRequest userUpdateRequest,
                                                UUID userUuid) {
    UserJpa user = findUser(userUuid);
    makeGroup(user, userUpdateRequest);
    return userResponseModelMapper.toDto(
        userService.updateUser(user)
    );
  }

  @Override
  public void updateStatusUserByUuid(UUID userUuid, StatusRequest status) {
    keycloakComponent.updateStatusUser(userUuid, status);
    UserJpa user = findUser(userUuid);
    user.setIsActive(status.getIsActive());
    userService.updateUser(user);
  }

  private UserJpa findUser(UUID userUuid) {
    return userService.findUserByUuid(userUuid)
        .orElseThrow(() -> new NotFoundException("person.not.found"));
  }

  private void makeGroup(UserJpa user, UserGroupRequest userUpdateRequest) {
    List<GroupJpa> currentGroups = new ArrayList<>(user.getGroups());
    List<GroupJpa> newGroups = listGroups(userUpdateRequest.getGroups());
    removeUser(currentGroups, newGroups, user);
    addUser(currentGroups, newGroups, user);
  }

  // I take the user's groups and the groups from the DTO,
  // check if in the request I don't have the group, then it means I'm no longer part of it,
  // and it deletes it from both the entity list and keycloak
  private void removeUser(List<GroupJpa> currentGroups, List<GroupJpa> newGroups, UserJpa user) {
    for (GroupJpa currentGroup : currentGroups) {
      if (!getGroupUuidFromGroups(newGroups).contains(currentGroup.getUuid())) {
        keycloakComponent.deleteUserGroup(user.getUuid(), currentGroup);
        user.getGroups().remove(currentGroup);
      }
    }
  }

  // I take the user's groups and groups from the DTO, check if in the request
  // I have the group, then I don't do anything, otherwise, if I don't have it,
  // it adds it either on the entity list or on keycloak
  private void addUser(List<GroupJpa> currentGroups, List<GroupJpa> newGroups, UserJpa user) {
    for (GroupJpa newGroup : newGroups) {
      if (!getGroupUuidFromGroups(currentGroups).contains(newGroup.getUuid())) {
        keycloakComponent.updateUser(user.getUuid(), newGroup);
        user.getGroups().add(newGroup);
      }
    }
  }

  private Set<UUID> getGroupUuidFromGroups(List<GroupJpa> group) {
    return group.stream()
        .map(GroupJpa::getUuid)
        .collect(Collectors.toSet());
  }

  private GroupRequest groupToGroupRequest() {
    return groupMapper.toRequest(groupService.findGroupByUuid(BASE_GROUP)
          .orElseThrow(() -> new NotFoundException("group.not.found"))
    );
  }

  private List<GroupJpa> listGroups(List<GroupRequest> groups) {
    return groups.stream().map(
            group -> groupService.findGroupByUuid(group.getUuid())
                .orElseThrow(() -> new NotFoundException("group.not.found")))
        .toList();
  }

  private UserJpa updateFields(UserUpdateRequest request, UserJpa user) {
    user.setBiography(
        request.getBiography() != null ? request.getBiography() : user.getBiography());
    user.setProfileImage(
        request.getProfileImage() != null ? request.getProfileImage() : user.getProfileImage());
    return user;
  }
}
