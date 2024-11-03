package com.efedorchenko.timely.service;

import com.efedorchenko.timely.entity.UserDetailsImpl;
import com.efedorchenko.timely.entity.UserEntity;
import com.efedorchenko.timely.model.auth.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class Mapper {

    private final PasswordEncoder passwordEncoder;

    public UserDetailsImpl toUserDetailsImpl(RegisterRequest registerRequest) {
        UserDetailsImpl user = new UserDetailsImpl();

        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        List<String> authorities = registerRequest.getAuthorities().stream().map(String::valueOf).toList();
        user.setAuthorities(authorities);

        return user;
    }

    public UserEntity toUserEntity(RegisterRequest registerRequest) {
        UserEntity user = new UserEntity();

        user.setName(registerRequest.getName());
        user.setPosition(registerRequest.getPosition());
        user.setRate(registerRequest.getRate());

        return user;
    }
}
