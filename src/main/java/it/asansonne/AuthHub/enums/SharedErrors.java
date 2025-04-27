package it.asansonne.authhub.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SharedErrors {
    ERROR ("Error: "),
    VALIDATION_ERROR ("Validation Error"),
    ERROR_DURING_JSON_SERIALIZATION ("Error during JSON serialization"),
    ERROR_DURING_JSON_DESERIALIZATION ("Error during JSON deserialization");

    private final String message;
  }
