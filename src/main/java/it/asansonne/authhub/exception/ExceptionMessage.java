package it.asansonne.authhub.exception;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * The type Exception message.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public final class ExceptionMessage {
  private HttpStatus status;
  private String message;
  private Map<String, String> validations;

  /**
   * Instantiates a new Exception message.
   *
   * @param status  the status
   * @param message the message
   */
  public ExceptionMessage(HttpStatus status, String message) {
    this.status = status;
    this.message = message;
  }
}

