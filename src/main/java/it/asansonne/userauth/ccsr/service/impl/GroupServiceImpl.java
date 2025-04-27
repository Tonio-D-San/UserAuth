package it.asansonne.userauth.ccsr.service.impl;

import static it.asansonne.userauth.enums.MessageConstant.GROUP_EMPTY;

import it.asansonne.userauth.ccsr.repository.jpa.GroupRepository;
import it.asansonne.userauth.ccsr.service.GroupService;
import it.asansonne.userauth.model.jpa.GroupJpa;
import jakarta.persistence.EntityNotFoundException;
import java.util.Optional;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * The type Group service.
 */
@Service
@AllArgsConstructor
public final class GroupServiceImpl implements GroupService {
  private final GroupRepository groupRepository;

  @Override
  public Optional<GroupJpa> findGroupByUuid(UUID uuid) {
    Optional<GroupJpa> group = groupRepository.findGroupByUuid(uuid);
    if (group.isEmpty()) {
      throw new EntityNotFoundException(GROUP_EMPTY.getMessage());
    }
    return group;
  }
}
