package it.asansonne.authhub.event;

public record OAuth2AuthHubAuthenticatedEvent(String email, String name) {
}