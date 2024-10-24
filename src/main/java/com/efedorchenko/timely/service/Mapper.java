package com.efedorchenko.timely.service;

import com.efedorchenko.timely.entity.UserDetailsImpl;
import com.efedorchenko.timely.entity.UserEntity;
import com.efedorchenko.timely.model.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class Mapper {

    private final PasswordEncoder passwordEncoder;

    public UserDetailsImpl toUserDetailsImpl(RegisterRequest registerRequest) {
        UserDetailsImpl user = new UserDetailsImpl();

//        user.setId(UUID.randomUUID());
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setAuthorities(registerRequest.getAuthorities());

        return user;
    }

    public UserEntity toUserEntity(RegisterRequest registerRequest) {
        UserEntity user = new UserEntity();

//        user.setId(UUID.randomUUID());
        user.setName(registerRequest.getName());
        user.setPosition(registerRequest.getPosition());
        user.setRate(registerRequest.getRate());

        return user;
    }
}
