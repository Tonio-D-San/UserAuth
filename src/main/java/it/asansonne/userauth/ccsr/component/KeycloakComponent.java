package it.asansonne.userauth.ccsr.component;

import it.asansonne.userauth.dto.request.UserRequest;
import it.asansonne.userauth.dto.request.StatusRequest;
import it.asansonne.userauth.model.jpa.GroupJpa;
import it.asansonne.userauth.model.jpa.UserJpa;
import java.util.UUID;

/**
 * The interface Keycloak component.
 */
public interface KeycloakComponent {

  /**
   * Read user user.
   *
   * @param email the email
   * @return the user
   */
  UserJpa readUser(String email);

  /**
   * Create a user.
   *
   * @param request the request
   */
  void createUser(UserRequest request);

  /**
   * Update user.
   *
   * @param userUuid the user uuid
   * @param request  the request
   */
  void updateUser(UUID userUuid, GroupJpa request);

  /**
   * Update status user.
   *
   * @param userUuid the user uuid
   * @param status   the status
   */
  void updateStatusUser(UUID userUuid, StatusRequest status);

  /**
   * Delete user group.
   *
   * @param userUuid the user uuid
   * @param group    the group
   */
  void deleteUserGroup(UUID userUuid, GroupJpa group);
}
