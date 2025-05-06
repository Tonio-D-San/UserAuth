package it.asansonne.authhub.constant;

import java.util.UUID;

/**
 * The interface Shared constant.
 */
public interface SharedConstant {
  String API = "api";
  String API_VERSION = "v1";
  String FIRST_ACCESS_ROLES = "hasRole('ROLE_client_first_access')";
  String ADMIN_ROLES = "hasRole('ROLE_client_admin')";
  String USER_ROLES = "hasRole('ROLE_client_user')";
  String ADMIN_USER_ROLES = "hasRole('ROLE_client_admin') or hasRole('ROLE_client_user')";
  String ADMIN = "admin-service-administrators";
  UUID BASE_GROUP = UUID.fromString("7de6d481-9da9-4bcc-aca2-30073030ae9d");
  UUID ADMIN_GROUP = UUID.fromString("f70783d9-b64b-46b9-81c2-e4af0e5f9889");
  UUID USER_GROUP = UUID.fromString("e246270c-acaa-4aed-8d9f-4b7bbe95f2cb");
  UUID FIRST_ACCESS_GROUP = UUID.fromString("c587b883-e508-43db-b633-f6231b955556");
}
