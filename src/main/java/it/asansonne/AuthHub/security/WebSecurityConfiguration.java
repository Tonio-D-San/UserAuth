package it.asansonne.authhub.security;

import static it.asansonne.authhub.constant.SharedConstant.API;
import static it.asansonne.authhub.constant.SharedConstant.API_VERSION;

import it.asansonne.authhub.ccsr.component.CustomOAuth2UserService;
import it.asansonne.authhub.exception.handler.AuthorizationAuthenticationHandler;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;

/**
 * The type Web security configuration.
 */
@Slf4j
@Configuration
@AllArgsConstructor
public class WebSecurityConfiguration {
  private AuthorizationAuthenticationHandler handler;

  /**
   * Token filter chain security filter chain.
   *
   * @param http                    the http
   * @param authenticationConverter the custom authenticationConverter for keycloak
   * @return the security filter chain
   */
  @Bean
  protected SecurityFilterChain filterChain(HttpSecurity http,
                                            KeycloakAuthenticationConverter authenticationConverter,
                                            CustomOAuth2UserService customOAuth2UserService)
      throws Exception {
    log.info("Configuring security filter chain");
    log.info("CustomOAuth2UserService: {}", customOAuth2UserService);
    return http.cors(Customizer.withDefaults())
        .oauth2ResourceServer(
            oauth2 ->
                oauth2.jwt(jwt ->
                    jwt.jwtAuthenticationConverter(authenticationConverter)))
        .oauth2Login(oauth2Login ->
            oauth2Login.userInfoEndpoint(userInfoEndpoint ->
                userInfoEndpoint.userService(customOAuth2UserService)))
        // State-less session (state in access-token only)
        .sessionManagement(sm ->
            sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        // Disable CSRF because of state-less session-management
        .csrf(AbstractHttpConfigurer::disable)
        .exceptionHandling(exceptionHandling -> exceptionHandling
            .authenticationEntryPoint(handler)
            .accessDeniedHandler(handler))
        .authorizeHttpRequests(
            requests -> requests
                .requestMatchers(new AntPathRequestMatcher(String.format("/%s/%s/**",
                    API, API_VERSION)))
                .authenticated()
                .anyRequest()
                .permitAll())
        .build();
  }

  /**
   * The Keycloak authentication converter.
   */
  @Component
  protected static class KeycloakAuthenticationConverter
      implements Converter<Jwt, JwtAuthenticationToken> {
    private final KeycloakAuthoritiesConverter authoritiesConverter;

    KeycloakAuthenticationConverter(KeycloakAuthoritiesConverter authoritiesConverter) {
      this.authoritiesConverter = authoritiesConverter;
    }

    @Override
    public JwtAuthenticationToken convert(@NonNull Jwt jwt) {
      return new JwtAuthenticationToken(jwt, authoritiesConverter.convert(jwt),
          List.of(
              jwt.getClaimAsString(StandardClaimNames.SUB)
          ).toString()
      );
    }
  }

  @Component
  static class KeycloakAuthoritiesConverter
      implements Converter<Jwt, List<SimpleGrantedAuthority>> {

    @Override
    @SuppressWarnings({"unchecked"})
    public List<SimpleGrantedAuthority> convert(Jwt jwt) {
      final var realmAccess = (Map<String, Object>) jwt.getClaims()
          .getOrDefault("resource_access", Map.of());
      final var client = (Map<String, Object>) realmAccess
          .getOrDefault("${keycloak.client-id.be}", Map.of());
      final var roles = (List<String>) client
          .getOrDefault("roles", List.of());
      final List<String> prefixRoles = roles.stream().map(s -> "ROLE_" + s).toList();
      final String clientScope = (String) jwt.getClaims()
          .getOrDefault("scope", "");
      final List<String> prefixScope = Arrays.stream(clientScope.split(" "))
          .map(s -> "SCOPE_" + s).toList();
      List<String> authorities = new ArrayList<>();
      authorities.addAll(prefixRoles);
      authorities.addAll(prefixScope);
      return authorities.stream().map(SimpleGrantedAuthority::new).toList();
    }
  }


//  @Component
//  @AllArgsConstructor
//  static class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
//
//    private final ApplicationEventPublisher eventPublisher;
//
//    @Override
//    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
//      OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
//      OAuth2User oAuth2User = delegate.loadUser(userRequest);
//
//      // Informazioni base
//      Map<String, Object> attributes = oAuth2User.getAttributes();
//      String email = (String) attributes.get("email");
//      String name = (String) attributes.get("name");
//
//      // Pubblica evento di autenticazione completata
//      eventPublisher.publishEvent(new OAuth2AuthHubenticatedEvent(email, name));
//
//      return oAuth2User;


}
