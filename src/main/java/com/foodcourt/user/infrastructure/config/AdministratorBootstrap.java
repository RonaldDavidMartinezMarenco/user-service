package com.foodcourt.user.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.foodcourt.user.domain.api.EnsureAdministrator;
import com.foodcourt.user.domain.model.NewAdministrator;

/**
 * Supplies the first administrator from the environment.
 * The password is hashed by the use case and is not written to the log.
 */
@Configuration
public class AdministratorBootstrap {

    @Bean
    ApplicationRunner ensureAdministrator(
            EnsureAdministrator ensure,
            @Value("${foodcourt.bootstrap.admin-first-name}") String firstName,
            @Value("${foodcourt.bootstrap.admin-last-name}") String lastName,
            @Value("${foodcourt.bootstrap.admin-identity-document}") String identityDocument,
            @Value("${foodcourt.bootstrap.admin-phone}") String phone,
            @Value("${foodcourt.bootstrap.admin-email}") String email,
            @Value("${foodcourt.bootstrap.admin-password}") String password) {
        return args -> ensure.ensure(new NewAdministrator(
                firstName,
                lastName,
                identityDocument,
                phone,
                email,
                password));
    }
}
