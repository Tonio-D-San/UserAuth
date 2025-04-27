package it.asansonne.authhub.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import it.asansonne.authhub.dto.Dto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * The type User request.
 */
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Representation of the User Request DTO")
public class UserRequest implements Dto {
  @NotBlank(message = "User username must not be null or empty")
  @Schema(
      description = "User username",
      name = "username",
      type = "String",
      example = "mrossi")
  private String username;

  @NotBlank(message = "User password must not be null or empty")
  @Size(
      min = 8,
      max = 32,
      message = "The user password must be between 8 and 32 characters")
  @Schema(
      description = "User password",
      name = "password",
      type = "String",
      example = "password")
  private String password;

  @NotBlank(message = "User email must not be null or empty")
  @Email(message = "Invalid user email format")
  @Size(
      min = 10,
      max = 100,
      message = "The user email must be between 10 and 100 characters")
  @Schema(
      description = "User email",
      name = "email",
      type = "String",
      example = "example@mail.it")
  private String email;

  @NotBlank(message = "User surname must not be null or empty")
  @Schema(
      description = "User surname",
      name = "lastname",
      type = "String",
      example = "Rossi")
  private String lastname;

  @NotBlank(message = "User name must not be null or empty")
  @Schema(
      description = "User name",
      name = "firstname",
      type = "String",
      example = "Mario")
  private String firstname;

  @NotNull(message = "User status must not be null or empty")
  @Schema(
      description = "User status",
      name = "isActive",
      type = "Boolean",
      example = "true")
  private Boolean isActive;

  @JsonProperty("groups")
  @Schema(
      description = "User status",
      name = "groups",
      type = "List<GroupRequest>")
  private List<GroupRequest> groups;

  @Schema(
      description = "User biography",
      name = "biography",
      type = "String",
      example = "This is a biography")
  private String biography;

  @Schema(
      hidden = true,
      description = "User profile image",
      name = "profileImage",
      type = "byte[]")
  private byte[] profileImage;
}