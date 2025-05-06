package it.asansonne.authhub.mapper;

import it.asansonne.authhub.dto.Dto;
import it.asansonne.authhub.model.Models;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

/**
 * The interface Request mapper.
 *
 * @param <D> Dto parameter
 * @param <M> Model parameter
 */
public interface RequestModelMapper<D extends Dto, M extends Models> {

  /**
   * To model m.
   *
   * @param dto the dto
   * @return the m
   */
  M toModel(D dto);

  /**
   * To model a list.
   *
   * @param dtos the dtos
   * @return the list
   */
  default List<M> toModel(Collection<D> dtos) {
    return dtos == null ? Collections.emptyList() :
        dtos.stream().map(this::toModel).toList();
  }

  /**
   * Convert a Dto with pageable into a model page.
   *
   * @param dtos     the dtos
   * @param pageable the pageable
   * @return the page
   */
  default Page<M> toModel(Page<D> dtos, Pageable pageable) {
    if (dtos == null) {
      return Page.empty();
    } else {
      List<M> modelList = dtos.stream()
          .map(this::toModel)
          .toList();
      return new PageImpl<>(modelList, pageable, dtos.getTotalElements());
    }
  }
}
