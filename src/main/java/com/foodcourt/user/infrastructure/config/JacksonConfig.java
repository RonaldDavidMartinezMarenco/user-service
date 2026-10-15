package com.foodcourt.user.infrastructure.config;

import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import tools.jackson.databind.cfg.CoercionAction;
import tools.jackson.databind.cfg.CoercionInputShape;
import tools.jackson.databind.cfg.MutableCoercionConfig;
import tools.jackson.databind.type.LogicalType;

/**
 * A JSON integer stays an integer. Text and decimals are not coerced, so
 * {@code "4"} and {@code 4.9} fail before a use case runs.
 */
@Configuration
public class JacksonConfig {

    @Bean
    JsonMapperBuilderCustomizer strictIntegers() {
        return builder -> builder.withCoercionConfig(LogicalType.Integer, JacksonConfig::rejectTextAndFloats);
    }

    private static void rejectTextAndFloats(MutableCoercionConfig config) {
        config.setCoercion(CoercionInputShape.String, CoercionAction.Fail);
        config.setCoercion(CoercionInputShape.Float, CoercionAction.Fail);
    }
}
