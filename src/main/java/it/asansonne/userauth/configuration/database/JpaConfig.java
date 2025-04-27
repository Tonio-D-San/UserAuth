package it.asansonne.userauth.configuration.database;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * The type Jpa config.
 */
@Configuration
@EnableJpaRepositories(basePackages = "it.asansonne.userauth.ccsr.repository.jpa")
@EntityScan(basePackages = "it.asansonne.userauth.model.jpa")
public class JpaConfig {
}
