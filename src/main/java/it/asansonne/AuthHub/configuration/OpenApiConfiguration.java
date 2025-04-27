package it.asansonne.authhub.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * The type Open api configuration.
 */
@Configuration
@OpenAPIDefinition(servers = {@Server(url = "http://localhost:8081", description = "AuthHub API")})
public class OpenApiConfiguration {
  private static final String SEC_SCHEME_OAUTH2 = "oauth2";
  @Value("${info.app.name}")
  private String appName;
  @Value("${server.host}")
  private String authServer;
  @Value("${keycloak.realm.name}")
  private String realm;

  /**
   * Custom open api open api.
   *
   * @param appDescription the app description
   * @param appVersion     the app version
   * @return the open api
   */
  @Bean
  public OpenAPI customOpenApi(@Value("${info.app.description}") String appDescription,
                               @Value("${info.app.version}") String appVersion) {
    ExternalDocumentation pdf = new ExternalDocumentation();
    var authUrl = getAuthUrl();
    var oauthFlows = new OAuthFlows().authorizationCode(new OAuthFlow()
        .authorizationUrl(authUrl + "/auth")
        .tokenUrl(authUrl + "/token"));
    return new OpenAPI()
        .components(new Components()
            .addSecuritySchemes(SEC_SCHEME_OAUTH2,
                new SecurityScheme()
                    .type(SecurityScheme.Type.OAUTH2)
                    .description("Oauth2 flow")
                    .flows(oauthFlows)))
        .security(Collections.singletonList(
            new SecurityRequirement().addList(SEC_SCHEME_OAUTH2)))
        .info(new Info()
            .version(appVersion)
            .title(appName)
            .description(appDescription))
        .externalDocs(pdf);
  }

  private String getAuthUrl() {
    return String.format("%s/realms/%s/protocol/openid-connect",
        this.authServer, this.realm);
  }
}
