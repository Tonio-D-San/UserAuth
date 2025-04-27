package it.asansonne.userauth.ccsr.service;

import it.asansonne.userauth.model.jpa.GroupJpa;
import java.util.Optional;
import java.util.UUID;

/**
 * The type Group service.
 */
public interface GroupService {

  /**
   * Find a group by uuid optional.
   *
   * @param uuid the uuid
   * @return the optional
   */
  Optional<GroupJpa> findGroupByUuid(UUID uuid);
}
