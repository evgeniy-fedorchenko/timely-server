package com.efedorchenko.timely.security;

import com.efedorchenko.timely.configuration.properties.JwtProperties;
import com.efedorchenko.timely.security.model.JwtTokenData;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
@Component
@RequiredArgsConstructor
public class JwtUtil {

    private static final String EMAIL_KEY = "email";
    private static final String ROLES_KEY = "roles";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtProperties properties;

    public static Optional<String> extractJwt(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(HttpHeaders.AUTHORIZATION))
                .filter(authHeader -> authHeader.startsWith(BEARER_PREFIX))
                .map(rawAuthorizationHeaderValue -> rawAuthorizationHeaderValue.substring(BEARER_PREFIX.length()));
    }

    public String generateToken(JwtTokenData tokenData) {
        Date now = new Date();

        return Jwts.builder()
                .subject(tokenData.getUserId().toString())
                .claim(ROLES_KEY, tokenData.getRoles())
                .claim(EMAIL_KEY, tokenData.getEmail())
                .signWith(getKey())
                .issuedAt(now)
//                .expiration(new Date(now.getTime() + properties.getExpirationMillis()))
                .compact();
    }

    public RawAuthenticationData parseToken(String rawToken) throws BadCredentialsException {
        try {

            Claims body = Jwts.parser()
                    .verifyWith(getKey())
                    .build()
                    .parseSignedClaims(rawToken)
                    .getPayload();

//            Date expiration = jws.getPayload().getExpiration();
//            if (expiration.before(new Date())) {
//                throw new AuthException("Expired JWT token");
//            }

            Collection<String> roles = extractRoles(body.get(ROLES_KEY, List.class));
            Collection<? extends GrantedAuthority> authorities = roles.isEmpty()
                    ? Collections.emptyList()
                    : roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());

            return new RawAuthenticationData(authorities, body.getSubject());

        } catch (JwtException | IllegalArgumentException ex) {
            throw new BadCredentialsException("Invalid JWT token", ex);
        }
    }

    @NotNull
    private Collection<String> extractRoles(Object body) {
        if (body instanceof Collection<?> collection && !collection.isEmpty()) {
            return collection.stream().map(Object::toString).toList();
        }
        return Collections.emptyList();
    }

    @NotNull
    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(properties.getSecret().getBytes());
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class RawAuthenticationData {

        private final Collection<? extends GrantedAuthority> authorities;
        private final String userId;
    }
}
