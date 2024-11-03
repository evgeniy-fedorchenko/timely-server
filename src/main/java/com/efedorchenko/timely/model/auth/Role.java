package com.efedorchenko.timely.model.auth;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;

public enum Role {

    WORKER,

    BOSS,

    CREATOR;

    public static List<Role> parse(Collection<? extends GrantedAuthority> authorities) {
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .map(Role::valueOf)
                .toList();
    }

}
