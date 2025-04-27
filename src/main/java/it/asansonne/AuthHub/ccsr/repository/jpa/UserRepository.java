package it.asansonne.authhub.ccsr.repository.jpa;

import it.asansonne.authhub.model.jpa.UserJpa;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * The interface User repository.
 */
@Repository
public interface UserRepository extends JpaRepository<UserJpa, Integer> {

  /**
   * Find user by uuid optional.
   *
   * @param uuid the uuid
   * @return the optional
   */
  Optional<UserJpa> findUserByUuid(UUID uuid);

  /**
   * Find by email optional.
   *
   * @param email the email
   * @return the optional
   */
  Optional<UserJpa> findByEmail(String email);

  /**
   * Find all by is active true page.
   *
   * @param pageable the pageable
   * @return the page
   */
  Page<UserJpa> findAllByIsActiveTrue(Pageable pageable);

  /**
   * Find all by is active false page.
   *
   * @param pageable the pageable
   * @return the page
   */
  Page<UserJpa> findAllByIsActiveFalse(Pageable pageable);
}
