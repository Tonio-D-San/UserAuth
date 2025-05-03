package it.asansonne.authhub.util;

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
      throw new IllegalArgumentException("error.json.serialization", e);
    }
  }

  @Override
  public Object convertToEntityAttribute(String dbData) {
    try {
      return objectMapper.readValue(dbData, Object.class);
    } catch (Exception e) {
      throw new IllegalArgumentException("error.json.deserialization", e);
    }
  }
}
