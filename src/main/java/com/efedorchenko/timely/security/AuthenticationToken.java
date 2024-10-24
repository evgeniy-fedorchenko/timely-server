package com.efedorchenko.timely.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import reactor.util.annotation.Nullable;

import java.util.Collection;

public class AuthenticationToken extends AbstractAuthenticationToken {

    private final String userId;

    /**
     * Creates a token with the supplied array of authorities.
     *
     * @param authorities the collection of {@link GrantedAuthority}s for the
     *                    principal represented by this authentication object
     */
    private AuthenticationToken(Collection<? extends GrantedAuthority> authorities, String userId) {
        super(authorities);
        this.userId = userId;
    }

    public static AuthenticationToken authenticate(Collection<? extends GrantedAuthority> authorities, String userId) {
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
     * Возвращает первичный ключ пользователя в строковом виде, для нахождения пользователя в базе данных.
     * Вы можете самостоятельно сходить в базу данных за полной информацией о пользователе
     *
     * @return идентификатор пользователя
     */
    @Override
    public Object getPrincipal() {
        return userId;
    }
}
