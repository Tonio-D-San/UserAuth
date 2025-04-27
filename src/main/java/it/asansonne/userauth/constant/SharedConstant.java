package it.asansonne.userauth.constant;

import java.util.UUID;

/**
 * The interface Shared constant.
 */
public interface SharedConstant {
  String API = "api";
  String API_VERSION = "v1";
  String ADMIN_ROLES = "hasRole('ROLE_client_admin')";
  String USER_ROLES = "hasRole('ROLE_client_user')";
  String ADMIN_USER_ROLES = "hasRole('ROLE_client_admin') or hasRole('ROLE_client_user')";
  String ADMIN = "admin-service-administrators";
  UUID DEFAULT_GROUP = UUID.fromString("7de6d481-9da9-4bcc-aca2-30073030ae9d");
}
