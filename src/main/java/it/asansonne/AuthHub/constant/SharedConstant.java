package it.asansonne.authhub.constant;

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

}
