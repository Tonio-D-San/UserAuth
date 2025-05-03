package it.asansonne.authhub.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

/**
 * The type App conf.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class AppConf {
}
