package it.asansonne.authhub.exception.handler;

import it.asansonne.authhub.exception.ExceptionMessage;
import it.asansonne.authhub.exception.custom.NotFoundException;
import it.asansonne.authhub.exception.custom.ParentCreationDateException;
import jakarta.persistence.EntityNotFoundException;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * The type Application exception handler.
 */
@Slf4j
@RestControllerAdvice
final class ApplicationExceptionHandler {

  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(RuntimeException.class)
  private ExceptionMessage runtimeExceptionHandler(RuntimeException e) {
    return new ExceptionMessage(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
  }

  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ExceptionHandler(EntityNotFoundException.class)
  private void handleEmptyException(EntityNotFoundException e) {
    log.error("Entity not found: {}", e.getMessage());
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(MethodArgumentNotValidException.class)
  private ExceptionMessage validationExceptionHandler(MethodArgumentNotValidException e) {
    Map<String, String> errors = new HashMap<>();
    e.getBindingResult().getAllErrors().forEach(error ->
      errors.put(((FieldError) error).getField(), error.getDefaultMessage())
    );
    return new ExceptionMessage(HttpStatus.BAD_REQUEST, "validation.error", errors);
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(HttpMessageNotReadableException.class)
  private ExceptionMessage validationExceptionHandler(HttpMessageNotReadableException e) {
    return new ExceptionMessage(HttpStatus.BAD_REQUEST, e.getMessage());
  }

  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ExceptionHandler({NoResourceFoundException.class, NotFoundException.class})
  private ExceptionMessage noResourceExceptionHandler(Exception e) {
    return new ExceptionMessage(HttpStatus.NOT_FOUND, e.getMessage());
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(IllegalArgumentException.class)
  private ExceptionMessage argumentErrorExceptionHandler(IllegalArgumentException e) {
    return new ExceptionMessage(HttpStatus.BAD_REQUEST, e.getMessage());
  }

  @ResponseStatus(HttpStatus.CONFLICT)
  @ExceptionHandler(DataIntegrityViolationException.class)
  private ExceptionMessage handleConflictException(DataIntegrityViolationException ex) {
    return new ExceptionMessage(HttpStatus.CONFLICT, ex.getMostSpecificCause().getMessage());
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(ParentCreationDateException.class)
  private ExceptionMessage handleParentCreationDateException(ParentCreationDateException ex) {
    return new ExceptionMessage(HttpStatus.BAD_REQUEST, ex.getMessage());
  }
}
