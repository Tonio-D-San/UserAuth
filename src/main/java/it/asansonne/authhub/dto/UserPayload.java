package it.asansonne.authhub.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserPayload implements Dto {
    private String username;
    private List<Credential> credentials;
    private List<String> requiredActions;
    private String email;
    private String lastName;
    private String firstName;
    private List<String> groups;
    private Boolean enabled;

    @Getter
    @Setter
    @Builder
    public static class Credential {
        private String type;
        private String value;
        private boolean temporary;
    }
}