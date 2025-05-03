package it.asansonne.authhub;

import lombok.Getter;

@Getter
public enum Attributes {
  COMPLETE("complete"),
  PENDING("pending");

  private final String value;

  Attributes(String value) {
    this.value = value;
  }
}
