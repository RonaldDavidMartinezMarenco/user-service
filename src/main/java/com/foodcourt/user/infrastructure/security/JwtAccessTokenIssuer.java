package com.foodcourt.user.infrastructure.security;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import com.foodcourt.user.domain.model.AccessToken;
import com.foodcourt.user.domain.model.User;
import com.foodcourt.user.domain.spi.AccessTokenIssuer;
import com.foodcourt.user.infrastructure.config.JwtConfig;

/** Signs an HS256 access token. The claims omit the email and the password hash. */
@Service
public final class JwtAccessTokenIssuer implements AccessTokenIssuer {

    private final JwtEncoder encoder;
    private final Clock clock;
    private final Duration ttl;

    public JwtAccessTokenIssuer(JwtEncoder encoder, Clock clock, Duration jwtTtl) {
        this.encoder = encoder;
        this.clock = clock;
        this.ttl = jwtTtl;
    }

    @Override
    public AccessToken issue(User user) {
        Instant issuedAt = clock.instant();
        Instant expiresAt = issuedAt.plus(ttl);
        String token = encoder.encode(JwtEncoderParameters.from(claims(user, issuedAt, expiresAt))).getTokenValue();
        return new AccessToken(token, ttl.toSeconds(), user.id(), user.role());
    }

    private static JwtClaimsSet claims(User user, Instant issuedAt, Instant expiresAt) {
        return JwtClaimsSet.builder()
                .issuer(JwtConfig.ISSUER)
                .audience(List.of(JwtConfig.AUDIENCE))
                .subject(Long.toString(user.id()))
                .issuedAt(issuedAt)
                .expiresAt(expiresAt)
                .claim(JwtConfig.ROLE_CLAIM, user.role().name())
                .build();
    }
}
