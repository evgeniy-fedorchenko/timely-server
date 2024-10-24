package com.efedorchenko.timely.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class JwtTokenData {

    private final UUID userId;

    private final Collection<? extends GrantedAuthority> authorities;

    private final String fullName;

    private final String email;

    public static JwtTokenData empty() {
        return new JwtTokenData(null, null, null, null);
    }
}
