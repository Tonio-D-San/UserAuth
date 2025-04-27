package it.asansonne.authhub.ccsr.service.impl;

import static it.asansonne.authhub.enums.MessageConstant.GROUP_EMPTY;

import it.asansonne.authhub.ccsr.repository.jpa.GroupRepository;
import it.asansonne.authhub.ccsr.service.GroupService;
import it.asansonne.authhub.model.jpa.GroupJpa;
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
