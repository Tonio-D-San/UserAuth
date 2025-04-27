package it.asansonne.authhub.exception.handler;

import static it.asansonne.authhub.enums.SharedErrors.ERROR;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.asansonne.authhub.exception.ExceptionMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.AllArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * The type Authorization and Authentication handler.
 */
@RestControllerAdvice
@AllArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AuthorizationAuthenticationHandler implements AccessDeniedHandler,
    AuthenticationEntryPoint {
  public static final String APPLICATION_JSON = "application/json";
  private final ObjectMapper objectMapper;

  @Override
  @ResponseStatus(HttpStatus.FORBIDDEN)
  @ExceptionHandler(AccessDeniedException.class)
  public void handle(HttpServletRequest request, HttpServletResponse response,
                     AccessDeniedException accessDeniedException)
      throws IOException {
    ExceptionMessage exceptionMessage = new ExceptionMessage(HttpStatus.FORBIDDEN,
        ERROR.getMessage() + accessDeniedException.getMessage());
    response.getWriter().write(objectMapper.writeValueAsString(exceptionMessage));
    response.setStatus(HttpStatus.FORBIDDEN.value());
    response.setContentType(APPLICATION_JSON);
  }

  @Override
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  @ExceptionHandler(AuthenticationException.class)
  public void commence(HttpServletRequest request, HttpServletResponse response,
                       AuthenticationException authException) throws IOException {
    ExceptionMessage exceptionMessage = new ExceptionMessage(HttpStatus.UNAUTHORIZED,
        ERROR.getMessage() + authException.getMessage());
    response.getWriter().write(objectMapper.writeValueAsString(exceptionMessage));
    response.setStatus(HttpStatus.UNAUTHORIZED.value());
    response.setContentType(APPLICATION_JSON);

  }


}
