package it.asansonne.authhub.ccsr.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Parameter;
import it.asansonne.authhub.dto.request.UserGroupRequest;
import it.asansonne.authhub.dto.request.UserRequest;
import it.asansonne.authhub.dto.request.UserUpdateRequest;
import it.asansonne.authhub.dto.request.StatusRequest;
import it.asansonne.authhub.dto.response.UserResponse;
import java.security.Principal;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * The interface User controller v1.
 */
public interface UserControllerV1 {
  /**
   * Find user by uuid user response.
   *
   * @param uuid the uuid
   * @return the user response
   */
  @GetMapping(value = "/{uuid}", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.OK)
  UserResponse findUserByUuid(@Parameter(name = "uuid", description = "The user uuid",
      example = "08fba211-60ca-45fc-b809-86bc2ad81dca") @PathVariable UUID uuid) throws JsonProcessingException;

  /**
   * Find all user's page.
   *
   * @param page      the page
   * @param size      the size
   * @param direction the direction
   * @return the page
   */
  @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.OK)
  Page<UserResponse> findAllUsers(
      @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
      @RequestParam(value = "size", required = false, defaultValue = "5") Integer size,
      @RequestParam(value = "direction", required = false, defaultValue = "asc") String direction
  );

  /**
   * Find active users page.
   *
   * @param page      the page
   * @param size      the size
   * @param direction the direction
   * @return the page
   */
  @GetMapping(value = "/active", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.OK)
  Page<UserResponse> findActiveUsers(
      @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
      @RequestParam(value = "size", required = false, defaultValue = "5") Integer size,
      @RequestParam(value = "direction", required = false, defaultValue = "asc") String direction
  );

  /**
   * Find inactive users page.
   *
   * @param page      the page
   * @param size      the size
   * @param direction the direction
   * @return the page
   */
  @GetMapping(value = "/inactive", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.OK)
  Page<UserResponse> findInactiveUsers(
      @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
      @RequestParam(value = "size", required = false, defaultValue = "5") Integer size,
      @RequestParam(value = "direction", required = false, defaultValue = "asc") String direction
  );

  /**
   * Create user response entity.
   *
   * @param userRequest the user request
   * @param builder     the builder
   * @return the response entity
   */
  @PostMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  ResponseEntity<UserResponse> createUser(UserRequest userRequest, UriComponentsBuilder builder
  ) throws JsonProcessingException;

  /**
   * Update user keycloak response.
   *
   * @param uuid the uuid
   * @return the user keycloak response
   */
  @PatchMapping(value = "/{uuid}", produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.OK)
  UserResponse updateUserByUuid(Principal principal, UserUpdateRequest userRequest,
                                @PathVariable("uuid") UUID uuid
  );

  /**
   * Update user keycloak response.
   *
   * @return the user keycloak response
   */
  @PatchMapping(value = "/me/create", produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.OK)
  UserResponse updateMe(Principal principal, UserUpdateRequest userRequest);

  /**
   * Update user keycloak response.
   *
   * @param uuid the uuid
   * @return the user keycloak response
   */
  @PatchMapping(value = "/groups/{uuid}", produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.OK)
  UserResponse updateGroupByUserUuid(UserGroupRequest userGroupRequest,
                                     @PathVariable("uuid") UUID uuid
  );

  /**
   * Update user status.
   *
   * @param uuid   the uuid
   * @param status the user status
   */
  @PatchMapping(value = "/status/{uuid}", produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.OK)
  void updateStatusUserByUuid(@PathVariable("uuid") UUID uuid, StatusRequest status);
}
