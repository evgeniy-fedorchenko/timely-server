package com.efedorchenko.timely.model.auth;

import com.efedorchenko.timely.entity.UserDetailsImpl;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.UUID;

@Getter
@ToString
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JwtTokenData {

    private final UUID userId;

    private final List<RoleType> roles;

    private final String email;

    public static JwtTokenData fromDetails(UserDetailsImpl userDetails) {
        return new JwtTokenData(
                userDetails.getId(),
                RoleType.parse(userDetails.getAuthorities()),
                userDetails.getUsername()
        );
    }
}
