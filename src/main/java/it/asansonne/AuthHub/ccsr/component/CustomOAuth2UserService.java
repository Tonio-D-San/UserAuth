package it.asansonne.authhub.ccsr.component;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

  private final UserComponent userComponent;

  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) {
    OAuth2User oAuth2User = new DefaultOAuth2UserService().loadUser(userRequest);

    // Informazioni base
    Map<String, Object> attributes = oAuth2User.getAttributes();
    log.info("Attributes: {}", attributes);
    String email = (String) attributes.get("email");
    String name = (String) attributes.get("name");

//    // Salvataggio su DB/Keycloak
//    userComponent.createUser(email, name);

    return oAuth2User;
  }
}