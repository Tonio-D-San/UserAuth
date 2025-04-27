package it.asansonne.authhub.util;

import static it.asansonne.authhub.constant.SharedConstant.ADMIN;

import it.asansonne.authhub.model.jpa.UserJpa;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import org.springframework.util.StringUtils;

/**
 * The type File util.
 */
public class FileUtil {

  private FileUtil() {
  }

  /**
   * Format file name string.
   *
   * @param originalFileName the original file name
   * @return the string
   */
  public static String formatFileName(String originalFileName) {
    return StringUtils.cleanPath(originalFileName).toLowerCase()
        .replace(" ", "-");
  }

  /**
   * Gets file name.
   *
   * @param fileName the file name
   * @return the file name
   */
  public static String getFileName(String fileName) {
    return fileName.substring(0, fileName.lastIndexOf("."));
  }

  /**
   * Gets extension.
   *
   * @param fileName the file name
   * @return the extension
   */
  public static String getExtension(String fileName) {
    return fileName.substring(fileName.lastIndexOf("."));
  }

  /**
   * Gets file name duplicate.
   *
   * @param fileName the file name
   * @return the file name duplicate
   */
  public static String getFileNameDuplicate(String fileName) {
    String onlyName = FileUtil.getFileName(fileName);
    if (onlyName.matches(".+\\(\\d+\\)$")) {
      return onlyName.substring(0, onlyName.lastIndexOf("("));
    } else {
      return onlyName;
    }
  }

  /**
   * Gets a file name from a complete path.
   *
   * @param completePath the complete path
   * @return the file name from a complete path
   */
  public static String getFileNameFromCompletePath(String completePath) {
    return completePath.substring(completePath.lastIndexOf("/") + 1);
  }

  /**
   * Gets path name from a complete path.
   *
   * @param completePath the complete path
   * @return the path name from a complete path
   */
  public static String getPathNameFromCompletePath(String completePath) {
    return completePath.substring(0, completePath.lastIndexOf("/")) + "/";
  }

  /**
   * Long to offset date time offset date time.
   *
   * @param creationDate the creation date
   * @return the offset date time
   */
  public static OffsetDateTime longToOffsetDateTime(Long creationDate) {
    return OffsetDateTime.ofInstant(Instant.ofEpochMilli(creationDate), ZoneOffset.UTC)
        .truncatedTo(ChronoUnit.MILLIS);
  }

  /**
   * Is admin boolean.
   *
   * @param user the user
   * @return the boolean
   */
  public static boolean isAdmin(UserJpa user) {
    return user.getGroups()
        .stream()
        .anyMatch(group ->
            group.getName()
                .equals(ADMIN)
        );
  }
}
