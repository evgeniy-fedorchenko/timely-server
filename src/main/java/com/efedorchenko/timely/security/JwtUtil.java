package com.efedorchenko.timely.security;

import com.efedorchenko.timely.configuration.JwtProperties;
import com.efedorchenko.timely.model.JwtTokenData;
import com.efedorchenko.timely.model.RegisterRequest;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
@Component
public class JwtUtil {

    private static final String FULLNAME_KEY = "fullName";
    private static final String EMAIL_KEY = "email";
    private static final String ROLES_KEY = "roles";

    private final JwtProperties properties;

    public @NotNull String generateToken(String userId, String fullName, String email, List<String> roles) {
        Date now = new Date();

        return Jwts.builder()
                .subject(userId)
                .claim(ROLES_KEY, roles)
                .claim(FULLNAME_KEY, fullName)
                .claim(EMAIL_KEY, email)
                .signWith(getKey())
                .issuedAt(now)
                .expiration(new Date(now.getTime() + properties.getExpirationMillis()))
                .compact();
    }

    public String generateToken(JwtTokenData tokenData) {
        Date now = new Date();

        return Jwts.builder()
                .subject(tokenData.getUserId().toString())
                .claim(ROLES_KEY, tokenData.getAuthorities())
                .claim(FULLNAME_KEY, tokenData.getFullName())
                .claim(EMAIL_KEY, tokenData.getEmail())
                .signWith(getKey())
                .issuedAt(now)
                .expiration(new Date(now.getTime() + properties.getExpirationMillis()))
                .compact();
    }

    public RawAuthenticationData parseToken(String rawToken) throws AuthException {
        try {
            SecretKey key = getKey();

            Jws<Claims> jws = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(rawToken);

            Date expiration = jws.getPayload().getExpiration();
            if (expiration.before(new Date())) {
                throw new AuthException("Expired JWT token");
            }

            Claims body = jws.getPayload();

            String userId = body.getSubject();
            List<String> roles = body.get(ROLES_KEY, List.class);

            Collection<? extends GrantedAuthority> authorities = roles == null
                    ? Collections.emptyList()
                    : roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());

            return new RawAuthenticationData(authorities, userId);

        } catch (JwtException | IllegalArgumentException e) {
            throw new AuthException("Invalid JWT token", e);
        }
    }

    @NotNull
    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(properties.getSecret().getBytes());
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PUBLIC)
    public static final class RawAuthenticationData {

        private final Collection<? extends GrantedAuthority> authorities;
        private final String userId;
    }
}
