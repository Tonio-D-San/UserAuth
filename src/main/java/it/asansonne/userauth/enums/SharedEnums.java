package it.asansonne.userauth.enums;

/**
 * The enum Shared errors.
 */
import lombok.Getter;
import lombok.RequiredArgsConstructor;
@Getter
@RequiredArgsConstructor
public enum SharedEnums {
    API ("api"),
    API_VERSION("v1");
    private final String message;
}
