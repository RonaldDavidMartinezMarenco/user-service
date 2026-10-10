package com.foodcourt.user.infrastructure.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import com.foodcourt.user.domain.exception.AuthenticationRequired;
import com.foodcourt.user.domain.model.Caller;
import com.foodcourt.user.domain.model.Role;
import com.foodcourt.user.domain.spi.CurrentCaller;
import com.foodcourt.user.infrastructure.config.JwtConfig;

/** Reads {@code sub} and {@code role} from the validated JWT. */
@Service
public final class JwtCurrentCaller implements CurrentCaller {

    @Override
    public Caller require() {
        return caller(jwt());
    }

    private static Jwt jwt() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!authenticatedJwt(authentication)) {
            throw new AuthenticationRequired();
        }
        return (Jwt) authentication.getPrincipal();
    }

    private static boolean authenticatedJwt(Authentication authentication) {
        return authentication != null
                && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof Jwt;
    }

    private static Caller caller(Jwt jwt) {
        return new Caller(userId(jwt), role(jwt));
    }

    private static long userId(Jwt jwt) {
        try {
            return Long.parseLong(jwt.getSubject());
        } catch (NumberFormatException failure) {
            throw new AuthenticationRequired();
        }
    }

    private static Role role(Jwt jwt) {
        String claim = jwt.getClaimAsString(JwtConfig.ROLE_CLAIM);
        if (claim == null) {
            throw new AuthenticationRequired();
        }
        return knownRole(claim);
    }

    private static Role knownRole(String claim) {
        try {
            return Role.valueOf(claim);
        } catch (IllegalArgumentException failure) {
            throw new AuthenticationRequired();
        }
    }
}
