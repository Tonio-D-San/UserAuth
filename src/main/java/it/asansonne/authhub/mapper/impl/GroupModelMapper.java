package it.asansonne.authhub.mapper.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.asansonne.authhub.ccsr.service.GroupService;
import it.asansonne.authhub.dto.request.GroupRequest;
import it.asansonne.authhub.dto.request.StatusRequest;
import it.asansonne.authhub.dto.request.UserRequest;
import it.asansonne.authhub.dto.response.GroupResponse;
import it.asansonne.authhub.exception.custom.NotFoundException;
import it.asansonne.authhub.mapper.RequestModelMapper;
import it.asansonne.authhub.mapper.ResponseModelMapper;
import it.asansonne.authhub.model.jpa.GroupJpa;
import it.asansonne.authhub.dto.UserPayload;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * The type User mapper.
 */
@Component
public class GroupModelMapper implements RequestModelMapper<GroupRequest, GroupJpa>,
    ResponseModelMapper<GroupJpa, GroupResponse> {

  private final GroupService groupService;

  public GroupModelMapper(GroupService groupService) {
    this.groupService = groupService;
  }

  @Override
  public GroupJpa toModel(GroupRequest dto) {
    if (dto == null) {
      return null;
    }
    return GroupJpa.builder()
        .uuid(dto.getUuid())
        .build();
  }

  @Override
  public GroupResponse toDto(GroupJpa model) {
    if (model == null) {
      return null;
    }
    return GroupResponse.builder()
        .uuid(model.getUuid())
        .name(model.getName())
        .path(model.getPath())
        .build();
  }

  @Override
  public GroupJpa dtoToModelResponse(GroupResponse dto) {
    if (dto == null) {
      return null;
    }
    return GroupJpa.builder()
        .uuid(dto.getUuid())
        .name(dto.getName())
        .path(dto.getPath())
        .build();
  }

  /**
   * To request group request.
   *
   * @param group the group
   * @return the group request
   */
  public GroupRequest toRequest(GroupJpa group) {
    if (group == null) {
      return null;
    }
    return GroupRequest.builder()
        .uuid(group.getUuid())
        .build();
  }

  public String setPayload(StatusRequest status) {
    return "{"
        + "\"enabled\": " + status.getIsActive()
        + "}";
  }

  public String setPayload(UserRequest request) {
    try {
      return new ObjectMapper().writeValueAsString(
          UserPayload.builder()
              .username(request.getUsername())
              .credentials(List.of(UserPayload.Credential.builder()
                  .type("password")
                  .value(request.getPassword())
                  .temporary(true)
                  .build()))
              .requiredActions(List.of("UPDATE_PASSWORD"))
              .email(request.getEmail())
              .lastName(request.getLastname())
              .firstName(request.getFirstname())
              .enabled(request.getIsActive())
              .groups(request.getGroups() != null && !request.getGroups().isEmpty()
                  ? List.of(groupJson(request.getGroups())
                  .toString()
                  .replace("[", "")
                  .replace("]", "")
                  .split(","))
                  : null)
              .build()
      );
    } catch (JsonProcessingException e) {
      throw new IllegalStateException("Errore durante la serializzazione del payload", e);
    }
  }

  private StringBuilder groupJson(List<GroupRequest> groups) {
    StringBuilder groupsJson = new StringBuilder("[");
    for (GroupRequest group : groups) {
      if (groupsJson.length() > 1) {
        groupsJson.append(",");
      }
      groupsJson.append("\"")
          .append(groupService.findGroupByUuid(group.getUuid())
              .orElseThrow(() -> new NotFoundException("group.not.found"))
              .getPath())
          .append("\"");
    }
    groupsJson.append("]");
    return groupsJson;
  }

}
