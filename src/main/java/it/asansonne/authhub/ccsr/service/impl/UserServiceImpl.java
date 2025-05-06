package it.asansonne.authhub.ccsr.service.impl;

import it.asansonne.authhub.ccsr.repository.jpa.UserRepository;
import it.asansonne.authhub.ccsr.service.UserService;
import it.asansonne.authhub.model.jpa.UserJpa;
import jakarta.persistence.EntityNotFoundException;
import java.util.Optional;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * The type User service.
 */
@Service
@AllArgsConstructor
public final class UserServiceImpl implements UserService {
  private final UserRepository userRepository;

  @Override
  public Optional<UserJpa> findUserByEmail(String email) {
    return userRepository.findByEmail(email);
  }

  @Override
  public Optional<UserJpa> findUserByUuid(UUID userUuid) {
    return userRepository.findUserByUuid(userUuid);
  }

  @Override
  public Page<UserJpa> findAllUsers(Pageable pageable) {
    Page<UserJpa> users = userRepository.findAll(pageable);
    if (users.isEmpty()) {
      throw new EntityNotFoundException("person.empty");
    }
    return users;
  }

  @Override
  public Page<UserJpa> findActiveUsers(Pageable pageable) {
    Page<UserJpa> users = userRepository.findAllByIsActiveTrue(pageable);
    if (users.isEmpty()) {
      throw new EntityNotFoundException("person.active.empty");
    }
    return users;
  }

  @Override
  public Page<UserJpa> findInactiveUsers(Pageable pageable) {
    Page<UserJpa> users = userRepository.findAllByIsActiveFalse(pageable);
    if (users.isEmpty()) {
      throw new EntityNotFoundException("person.inactive.empty");
    }
    return users;
  }

  @Override
  public UserJpa updateUser(UserJpa user) {
    return userRepository.save(user);
  }
}
