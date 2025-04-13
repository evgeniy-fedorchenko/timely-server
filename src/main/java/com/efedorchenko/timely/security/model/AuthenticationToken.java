package com.efedorchenko.timely.security.model;

import jakarta.annotation.Nullable;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.UUID;

public class AuthenticationToken extends AbstractAuthenticationToken {

    private final UUID userId;

    /**
     * Creates a token with the supplied array of authorities.
     *
     * @param authorities the collection of {@link GrantedAuthority}s for the
     *                    principal represented by this authentication object
     */
    private AuthenticationToken(Collection<? extends GrantedAuthority> authorities, UUID userId) {
        super(authorities);
        this.userId = userId;
    }

    public static AuthenticationToken authenticate(Collection<? extends GrantedAuthority> authorities, UUID userId) {
        AuthenticationToken authenticationToken = new AuthenticationToken(authorities, userId);
        authenticationToken.setAuthenticated(true);
        return authenticationToken;
    }

    /**
     * Всегда возвращает {@code null} так как JWT-токен не содержит учетных данных пользователя
     *
     * @return {@code null}
     */
    @Override
    public @Nullable Object getCredentials() {
        return null;
    }

    /**
     * Возвращает первичный ключ пользователя в типе {@link UUID}, для нахождения пользователя в базе данных.
     * Вы можете самостоятельно сходить в базу данных за полной информацией о пользователе
     *
     * @return идентификатор пользователя
     */
    @Override
    public UUID getPrincipal() {
        return userId;
    }
}
