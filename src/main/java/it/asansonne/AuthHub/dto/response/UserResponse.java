package it.asansonne.authhub.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import it.asansonne.authhub.dto.Dto;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * The type User response.
 */
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Representation of the User Response DTO")
public class UserResponse implements Dto {
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  @Schema(
      description = "User uuid",
      name = "userUuid",
      type = "UUID",
      example = "08fba211-60ca-45fc-b809-86bc2ad81dca")
  private UUID id;

  @Schema(
      description = "User username",
      name = "username",
      type = "String",
      example = "username")
  private String username;

  @Schema(
      description = "User email",
      name = "email",
      type = "String",
      example = "example@domain.it")
  private String email;

  @Schema(
      description = "User name",
      name = "name",
      type = "String",
      example = "Mario")
  private String firstName;

  @Schema(
      description = "User surname",
      name = "surname",
      type = "String",
      example = "Rossi")
  private String lastName;

  @Schema(
      description = "User biography",
      name = "biography",
      type = "String",
      example = "This is a biography")
  private String biography;

  @Schema(
      description = "List of groups",
      name = "groups",
      type = "List<GroupResponse>")
  private List<GroupResponse> groups;

  @Schema(
      description = "Attributes of the user",
      name = "attributes",
      type = "List<String>",
      example = "[complete, pending]")
  private Map<String, List<String>> attributes;

  @Schema(
      description = "User active toggle",
      name = "isActive",
      type = "String",
      example = "true")
  private Boolean enabled;

  @Schema(
      hidden = true,
      description = "User profile image",
      name = "profileImage",
      type = "byte[]")
  private byte[] profileImage;
}
