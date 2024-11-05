package com.efedorchenko.timely.model.auth;

import com.efedorchenko.timely.entity.UserDetailsImpl;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import reactor.util.annotation.Nullable;

import java.util.List;
import java.util.UUID;

@Getter
@ToString
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class JwtTokenData {

    private final UUID userId;

    private final List<Role> authorities;

    private final String email;

    @Nullable
    private final AuthFailReason authFailReason;

    public static JwtTokenData failWith(AuthFailReason authFailReason) {
        return new JwtTokenData(null, null, null, authFailReason);
    }

    public static JwtTokenData fromDetails(UserDetailsImpl userDetails) {
        return new JwtTokenData(
                userDetails.getId(),
                Role.parse(userDetails.getAuthorities()),
                userDetails.getUsername(),
                null
        );
    }
}
