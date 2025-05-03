package it.asansonne.authhub.ccsr.service.impl;

import it.asansonne.authhub.ccsr.repository.jpa.GroupRepository;
import it.asansonne.authhub.ccsr.service.GroupService;
import it.asansonne.authhub.model.jpa.GroupJpa;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


/**
 * The type Group service.
 */
@Service
@RequiredArgsConstructor
public final class GroupServiceImpl implements GroupService {
  private final GroupRepository groupRepository;

  @Override
  public Optional<GroupJpa> findGroupByUuid(UUID uuid) {
    Optional<GroupJpa> group = groupRepository.findGroupByUuid(uuid);
    if (group.isEmpty()) {
      throw new EntityNotFoundException("group.empty");
    }
    return group;
  }

  @Override
  public List<GroupJpa> findAllByUuidIn(List<UUID> uuidList) {
    return groupRepository.findAllByUuidIn(uuidList);
  }
}
