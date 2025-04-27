package it.asansonne.userauth.configuration.database;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * The type Jpa config.
 */
@Configuration
@EnableJpaRepositories(basePackages = "${base.package}"+".ccsr.repository.jpa")
@EntityScan(basePackages = "${base.package}"+".model.jpa")
public class JpaConfig {
}
