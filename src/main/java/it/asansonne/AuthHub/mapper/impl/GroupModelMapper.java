package it.asansonne.authhub.mapper.impl;

import it.asansonne.authhub.dto.request.GroupRequest;
import it.asansonne.authhub.dto.response.GroupResponse;
import it.asansonne.authhub.mapper.RequestModelMapper;
import it.asansonne.authhub.mapper.ResponseModelMapper;
import it.asansonne.authhub.model.jpa.GroupJpa;
import org.springframework.stereotype.Component;

/**
 * The type User mapper.
 */
@Component
public class GroupModelMapper implements RequestModelMapper<GroupRequest, GroupJpa>,
    ResponseModelMapper<GroupJpa, GroupResponse> {

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
}
