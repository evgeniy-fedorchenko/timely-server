package com.efedorchenko.timely.model.auth;

import com.efedorchenko.timely.entity.UserDetailsImpl;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.UUID;

@Getter
@ToString
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class JwtTokenData {

    private final UUID userId;

    private final List<Role> authorities;

    private final String email;

    public static JwtTokenData empty() {
        return new JwtTokenData(null, null, null);
    }

    public static JwtTokenData fromDetails(UserDetailsImpl userDetails) {
        return new JwtTokenData(
                userDetails.getId(),
                Role.parse(userDetails.getAuthorities()),
                userDetails.getUsername()
        );
    }
}
