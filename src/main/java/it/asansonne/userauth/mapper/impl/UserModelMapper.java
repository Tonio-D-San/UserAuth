package it.asansonne.userauth.mapper.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.asansonne.userauth.dto.request.UserRequest;
import it.asansonne.userauth.dto.response.UserResponse;
import it.asansonne.userauth.exception.custom.NotFoundException;
import it.asansonne.userauth.mapper.RequestModelMapper;
import it.asansonne.userauth.mapper.ResponseModelMapper;
import it.asansonne.userauth.model.jpa.UserJpa;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * The type User mapper.
 */
@Component
public class UserModelMapper implements RequestModelMapper<UserRequest, UserJpa>,
    ResponseModelMapper<UserJpa, UserResponse> {

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
      throw new IllegalArgumentException("Error during JSON deserialization", e);
    }
  }
}
