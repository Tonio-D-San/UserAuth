package it.asansonne.authhub.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * The interface Message constant.
 */
@Getter
@RequiredArgsConstructor
public enum MessageConstant {
  PERSON_NOT_FOUND ("No user found"),
  PERSON_EMPTY ("User list is empty"),
  PERSON_INACTIVE_EMPTY ("User inactive list is empty"),
  PERSON_ACTIVE_EMPTY ("User active list is empty"),
  FORBIDDEN ("Access is prohibited"),
  JWT_ERROR ("No valid JWT token found in the security context"),
  GROUP_EMPTY ("Group list is empty"),
  GROUP_NOT_FOUND ("No group found"),
  NOT_ALLOWED ("Method not allowed");
    private final String message;
}
