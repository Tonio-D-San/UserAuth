package it.asansonne.userauth.util;

import static it.asansonne.userauth.enums.SharedErrors.ERROR_DURING_JSON_DESERIALIZATION;
import static it.asansonne.userauth.enums.SharedErrors.ERROR_DURING_JSON_SERIALIZATION;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * The type JSON converter.
 */
@Converter
public class JsonConverter implements AttributeConverter<Object, String> {
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public String convertToDatabaseColumn(Object attribute) {
    try {
      return objectMapper.writeValueAsString(attribute);
    } catch (Exception e) {
      throw new IllegalArgumentException(ERROR_DURING_JSON_SERIALIZATION.getMessage(), e);
    }
  }

  @Override
  public Object convertToEntityAttribute(String dbData) {
    try {
      return objectMapper.readValue(dbData, Object.class);
    } catch (Exception e) {
      throw new IllegalArgumentException(ERROR_DURING_JSON_DESERIALIZATION.getMessage(), e);
    }
  }
}
