package it.asansonne.authhub.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import it.asansonne.authhub.dto.Dto;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * The type Group response.
 */
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Representation of the Group Response DTO")
public class GroupResponse implements Dto {
  @JsonProperty("uuid")
  @Schema(
      description = "Group uuid",
      name = "uuid",
      type = "UUID",
      example = "d8317c61-1ca9-4a3d-9501-ec70e74e50e6")
  private UUID uuid;

  @JsonProperty("name")
  @Schema(
      description = "Category name",
      name = "name",
      type = "String",
      example = "Java")
  private String name;

  @JsonProperty("path")
  @Schema(
      description = "Category path",
      name = "path",
      type = "String",
      example = "/application-users/admin-service-administrators")
  private String path;
}
