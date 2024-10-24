package com.efedorchenko.timely.security;

import com.efedorchenko.timely.repository.UserDetailsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ReactiveUserDetailsServiceImpl implements ReactiveUserDetailsService {

    private final UserDetailsRepository userDetailsRepository;

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return userDetailsRepository.findByUsername(username);
    }
}
