package it.asansonne.authhub.ccsr.component;

import it.asansonne.authhub.dto.request.UserGroupRequest;
import it.asansonne.authhub.dto.request.UserRequest;
import it.asansonne.authhub.dto.request.UserUpdateRequest;
import it.asansonne.authhub.dto.request.StatusRequest;
import it.asansonne.authhub.dto.response.UserResponse;
import java.security.Principal;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * The interface User component.
 */
public interface UserComponent {

  /**
   * Find user by uuid user response.
   *
   * @param userUuid the user uuid
   * @return the user response
   */
  UserResponse findUserByUuid(UUID userUuid);

  /**
   * Find all user's pages.
   *
   * @param pageable the pageable
   * @return the page
   */
  Page<UserResponse> findAllUsers(Pageable pageable);

  /**
   * Find active users page.
   *
   * @param pageable the pageable
   * @return the page
   */
  Page<UserResponse> findActiveUsers(Pageable pageable);

  /**
   * Find inactive users page.
   *
   * @param pageable the pageable
   * @return the page
   */
  Page<UserResponse> findInactiveUsers(Pageable pageable);

  /**
   * Create a user response.
   *
   * @param userRequest the keycloak user request
   * @return the user response
   */
  UserResponse createUser(UserRequest userRequest);

  /**
   * Update user response.
   *
   * @param principal         the principal
   * @param userUpdateRequest the user request
   * @param userUuid          the user uuid
   * @return the user response
   */
  UserResponse updateUserByUuid(Principal principal, UserUpdateRequest userUpdateRequest,
                                    UUID userUuid);

  /**
   * Update me user response.
   *
   * @param principal   the principal
   * @param userRequest the user request
   * @return the user response
   */
  UserResponse updateMe(Principal principal, UserUpdateRequest userRequest);

  /**
   * Update user response.
   *
   * @param userGroupRequest the user group request
   * @param userUuid         the user uuid
   * @return the user response
   */
  UserResponse updateGroupByUserUuid(UserGroupRequest userGroupRequest,
                                         UUID userUuid);

  /**
   * Update status user.
   *
   * @param userUuid the user uuid
   * @param status   the user status
   */
  void updateStatusUserByUuid(UUID userUuid, StatusRequest status);
}
