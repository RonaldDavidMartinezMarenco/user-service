package com.foodcourt.user.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "foodcourt.clients")
public record ServiceClientsProperties(String plazaServiceUrl) {
}
