package it.asansonne.authhub.util;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * The type Principal data.
 */
@Getter
@AllArgsConstructor
public class PrincipalData {
  private UUID uuid;
  private String email;
  private String surname;
  private String name;
  private String roleAdmin;
  private String roleUser;

  /**
   * Gets date.
   *
   * @param principal the principal
   * @return the date
   */
  public static PrincipalData getData(Principal principal) {
    Matcher matcher =
        Pattern.compile("ROLE_client_(\\w+)|[^,\\[\\]\\s]+").matcher(principal.getName());
    List<String> partsList = new ArrayList<>();
    String roleAdmin = "";
    String roleUser = "";
    while (matcher.find()) {
      String match = matcher.group();
      if (!match.startsWith("SCOPE_")) {
        if (matcher.group(1) != null) {
          String role = matcher.group(1);
          if (role.equalsIgnoreCase("admin")) {
            roleAdmin = role;
          } else if (role.equalsIgnoreCase("user")) {
            roleUser = role;
          }
        } else {
          partsList.add(match);
        }
      }
    }
    String[] parts = partsList.toArray(new String[0]);
    return new PrincipalData(
        UUID.fromString(parts[0]), // UUID
        parts.length > 1 ? parts[1] : "", // Email
        parts.length > 2 ? parts[2] : "", // Cognome
        parts.length > 3 ? parts[3] : "", // Nome
        roleAdmin,
        roleUser
    );
  }
}
