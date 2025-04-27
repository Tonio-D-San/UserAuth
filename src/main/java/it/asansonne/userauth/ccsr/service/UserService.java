package it.asansonne.userauth.ccsr.service;

import it.asansonne.userauth.model.jpa.UserJpa;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * The interface User service.
 */
public interface UserService {

  /**
   * Find a user by email optional.
   *
   * @param email the email
   * @return the optional
   */
  Optional<UserJpa> findUserByEmail(String email);

  /**
   * Find user by uuid optional.
   *
   * @param userUuid the user uuid
   * @return the optional
   */
  Optional<UserJpa> findUserByUuid(UUID userUuid);

  /**
   * Find all user's page.
   *
   * @param pageable the pageable
   * @return the page
   */
  Page<UserJpa> findAllUsers(Pageable pageable);

  /**
   * Find active users page.
   *
   * @param pageable the pageable
   * @return the page
   */
  Page<UserJpa> findActiveUsers(Pageable pageable);

  /**
   * Find inactive users page.
   *
   * @param pageable the pageable
   * @return the page
   */
  Page<UserJpa> findInactiveUsers(Pageable pageable);

  /**
   * Update user.
   *
   * @param user the user
   * @return the user
   */
  UserJpa updateUser(UserJpa user);
}
