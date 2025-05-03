package it.asansonne.authhub.ccsr.service.impl;

import static it.asansonne.authhub.util.RestUtil.setHeader;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.asansonne.authhub.ccsr.repository.jpa.GroupRepository;
import it.asansonne.authhub.ccsr.service.GroupService;
import it.asansonne.authhub.model.jpa.GroupJpa;
import jakarta.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * The type Group service.
 */
@Service
@RequiredArgsConstructor
public final class GroupServiceImpl implements GroupService {
  @Value("${keycloak.host.groups}")
  private String urlGroups;
  private final RestTemplate restTemplate = new RestTemplate();
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
  public GroupJpa findByPathContainingIgnoreCase(String path) {
    return groupRepository.findByPathContainingIgnoreCase(path)
        .orElseThrow(() -> new EntityNotFoundException("group.empty"));
  }

  public List<GroupJpa> fetchAllGroups(JwtAuthenticationToken jwtAuthToken) {
    List<GroupJpa> groups = new ArrayList<>();
    fetchGroupsRecursively(urlGroups, jwtAuthToken, groups);
    return groups;
  }

  private void fetchGroupsRecursively(String url, JwtAuthenticationToken jwtAuthToken,
                                      List<GroupJpa> groups) {
    try {
      for (JsonNode groupNode :
          new ObjectMapper()
              .readTree(restTemplate.exchange(
                  url,
                  HttpMethod.GET,
                  new HttpEntity<>("{}", setHeader(jwtAuthToken)),
                  String.class
              ).getBody())
      ) {
        GroupJpa group = new GroupJpa();
        group.setUuid(UUID.fromString(groupNode.get("id").asText()));
        group.setName(groupNode.get("name").asText());
        group.setPath(groupNode.get("path").asText());
        groups.add(group);
        if (groupNode.get("subGroupCount").asInt() > 0) {
          fetchGroupsRecursively(
              urlGroups + "/" + group.getUuid() + "/children",
              jwtAuthToken,
              groups
          );
        }
      }
    } catch (Exception e) {
      throw new IllegalStateException("error.get.groups", e);
      //TODO: cambiare eccezione e usare messaggi di errore dalle properties
    }
  }

}
