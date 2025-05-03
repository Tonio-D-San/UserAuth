package it.asansonne.authhub.mapper.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.asansonne.authhub.dto.request.UserRequest;
import it.asansonne.authhub.dto.request.UserUpdateRequest;
import it.asansonne.authhub.dto.response.GroupResponse;
import it.asansonne.authhub.dto.response.UserResponse;
import it.asansonne.authhub.mapper.RequestModelMapper;
import it.asansonne.authhub.mapper.ResponseModelMapper;
import it.asansonne.authhub.model.jpa.GroupJpa;
import it.asansonne.authhub.model.jpa.UserJpa;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * The type User mapper.
 */
@Component
@RequiredArgsConstructor
public class UserModelMapper implements RequestModelMapper<UserRequest, UserJpa>,
    ResponseModelMapper<UserJpa, UserResponse> {

  private final GroupModelMapper groupModelMapper;

  @Override
  public UserJpa toModel(UserRequest dto) {
    if (dto == null) {
      return null;
    }
    return UserJpa.builder()
        .biography(dto.getBiography())
        .build();
  }

  @Override
  public UserResponse toDto(UserJpa model) {
    if (model == null) {
      return null;
    }
    return UserResponse.builder()
        .id(model.getUuid())
        .email(model.getEmail())
        .firstName(model.getName())
        .lastName(model.getSurname())
        .biography(model.getBiography())
        .enabled(model.getIsActive())
        .groups(model.getGroups() != null ? groupModelMapper.toDto(model.getGroups()) : null)
        .build();
  }

  @Override
  public UserJpa dtoToModelResponse(UserResponse dto) {
    if (dto == null) {
      return null;
    }
    return UserJpa.builder()
        .uuid(dto.getId())
        .email(dto.getEmail())
        .name(dto.getFirstName())
        .surname(dto.getLastName())
        .biography(dto.getBiography())
        .isActive(dto.getEnabled())
        .build();
  }

  /**
   * JSON to a dto list.
   *
   * @param json the JSON
   * @return the list
   */
  public List<UserResponse> jsonToDto(String json) {
    try {
      return List.of(new ObjectMapper().readValue(json, UserResponse[].class));
    } catch (JsonProcessingException e) {
      throw new IllegalArgumentException("error.json.deserialization", e);
    }
  }

  public UserResponse myJsonToDto(String json, UserUpdateRequest userUpdateRequest, GroupJpa group) {
    try {
      JsonNode rootNode = new ObjectMapper().readTree(json);
      return UserResponse.builder()
          .id(UUID.fromString(rootNode.get("id").asText()))
          .username(rootNode.get("username").asText())
          .email(rootNode.get("email").asText())
          .firstName(rootNode.get("firstName").asText())
          .lastName(rootNode.get("lastName").asText())
          .enabled(rootNode.get("enabled").asBoolean())
          .biography(userUpdateRequest.getBiography())
          .groups(
              List.of(GroupResponse.builder()
              .uuid(group.getUuid())
              .name(group.getName())
              .path(group.getPath())
              .build()))
          .build();
    } catch (Exception e) {
      throw new IllegalArgumentException("error.json.deserialization", e);
    }
  }
}
