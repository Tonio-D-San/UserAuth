package it.asansonne.authhub.exception.custom;

/**
 * The type Inactive user exception.
 */
public class InactiveUserException extends RuntimeException {

  /**
   * Instantiates a new Inactive user exception.
   */
  @SuppressWarnings("unused")
  public InactiveUserException() {
    super();
  }

  /**
   * Instantiates a new Inactive user exception.
   *
   * @param message the message
   */
  public InactiveUserException(String message) {
    super(message);
  }
}
