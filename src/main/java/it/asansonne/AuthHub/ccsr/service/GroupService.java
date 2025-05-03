package it.asansonne.authhub.ccsr.service;

import it.asansonne.authhub.model.jpa.GroupJpa;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

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

  GroupJpa findByPathContainingIgnoreCase(String path);

  List<GroupJpa> fetchAllGroups(JwtAuthenticationToken jwtAuthToken);
}
