package com.foodcourt.user.infrastructure.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import com.foodcourt.user.domain.model.AccessToken;
import com.foodcourt.user.domain.model.Role;
import com.foodcourt.user.domain.model.User;
import com.foodcourt.user.infrastructure.config.JwtConfig;

class JwtAccessTokenIssuerTest {

    private static final String SECRET = "0123456789abcdef0123456789abcdef";
    private static final Clock NOW = Clock.fixed(Instant.now().plus(Duration.ofHours(2)), ZoneOffset.UTC);

    @Test
    void issueProducesATokenTheDecoderAccepts() {
        SecretKey key = secret();
        JwtAccessTokenIssuer issuer = new JwtAccessTokenIssuer(encoder(key), NOW, Duration.ofHours(8));

        AccessToken token = issuer.issue(account());
        Jwt jwt = decoder(key).decode(token.value());

        assertThat(token.expiresInSeconds()).isEqualTo(28800L);
        assertThat(token.userId()).isEqualTo(15L);
        assertThat(token.role()).isEqualTo(Role.OWNER);
        assertThat(jwt.getSubject()).isEqualTo("15");
        assertThat(jwt.getClaimAsString("role")).isEqualTo("OWNER");
        assertThat(jwt.getClaimAsString("iss")).isEqualTo(JwtConfig.ISSUER);
        assertThat(JwtRoleConverter.create().convert(jwt).getAuthorities())
                .extracting(GrantedAuthority::getAuthority)
                .contains("ROLE_OWNER");
    }

    @Test
    void issueIsRejectedAfterItExpires() {
        SecretKey key = secret();
        Clock past = Clock.fixed(Instant.parse("2020-01-01T00:00:00Z"), ZoneOffset.UTC);
        JwtAccessTokenIssuer issuer = new JwtAccessTokenIssuer(encoder(key), past, Duration.ofHours(1));

        AccessToken token = issuer.issue(account());

        assertThatThrownBy(() -> decoder(key).decode(token.value())).isInstanceOf(JwtException.class);
    }

    private static SecretKey secret() {
        return new SecretKeySpec(SECRET.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    }

    private static JwtEncoder encoder(SecretKey key) {
        return NimbusJwtEncoder.withSecretKey(key).build();
    }

    private static JwtDecoder decoder(SecretKey key) {
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withSecretKey(key).macAlgorithm(MacAlgorithm.HS256).build();
        decoder.setJwtValidator(new org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator<>(
                org.springframework.security.oauth2.jwt.JwtValidators.createDefaultWithIssuer(JwtConfig.ISSUER),
                new org.springframework.security.oauth2.jwt.JwtClaimValidator<>(
                        org.springframework.security.oauth2.jwt.JwtClaimNames.AUD,
                        JwtAccessTokenIssuerTest::audienceMatches)));
        return decoder;
    }

    private static boolean audienceMatches(java.util.List<String> audience) {
        return audience != null && audience.contains(JwtConfig.AUDIENCE);
    }

    private static User account() {
        return new User(
                15L,
                "María",
                "Ana2",
                "1234567890",
                "+573005698325",
                LocalDate.of(1990, 5, 1),
                "ana@example.com",
                "hashed-secret12",
                Role.OWNER,
                null);
    }
}
