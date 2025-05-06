package it.asansonne.authhub.util.swagger.schema;

import it.asansonne.authhub.dto.response.UserResponse;
import java.util.List;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

/**
 * The type Page User schema.
 * Don't use this class into the project
 * It is only a schema for Swagger documentation.
 */
public final class PageUserSchema extends PageImpl<UserResponse> {

  /**
   * Instantiates a new Page user schema.
   *
   * @param content  the content
   * @param pageable the pageable
   * @param total    the total
   */
  @SuppressWarnings("unused")
  public PageUserSchema(List<UserResponse> content, Pageable pageable, long total) {
    super(content, pageable, total);
  }

  /**
   * Instantiates a new Page user schema.
   *
   * @param content the content
   */
  @SuppressWarnings("unused")
  public PageUserSchema(List<UserResponse> content) {
    super(content);
  }
}
