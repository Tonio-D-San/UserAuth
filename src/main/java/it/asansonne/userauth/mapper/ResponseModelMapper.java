package it.asansonne.userauth.mapper;

import it.asansonne.userauth.dto.Dto;
import it.asansonne.userauth.model.Models;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

/**
 * The interface Response mapper.
 *
 * @param <M> Model parameter
 * @param <D> Dto parameter
 */
public interface ResponseModelMapper<M extends Models, D extends Dto> {

  /**
   * To a dto list.
   *
   * @param models the models
   * @return the list
   */
  default List<D> toDto(Collection<M> models) {
    return models == null ? Collections.emptyList() :
        models.stream().map(this::toDto).collect(Collectors.toList());
  }

  /**
   * To a dto page.
   *
   * @param models   the models
   * @param pageable the pageable
   * @return the page
   */
  default Page<D> toDto(Page<M> models, Pageable pageable) {
    if (models == null) {
      return Page.empty();
    } else {
      List<D> dtoList = models.stream()
          .map(this::toDto)
          .toList();
      return new PageImpl<>(dtoList, pageable, models.getTotalElements());
    }
  }

  /**
   * To dto d.
   *
   * @param model the model
   * @return the d
   */
  D toDto(M model);

  /**
   * Dto to model response m.
   *
   * @param dto the dto
   * @return the m
   */
  M dtoToModelResponse(D dto);
}
