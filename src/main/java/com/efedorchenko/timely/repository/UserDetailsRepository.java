package com.efedorchenko.timely.repository;

import com.efedorchenko.timely.entity.UserDetailsImpl;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.security.core.userdetails.UserDetails;
import reactor.core.publisher.Mono;

public interface UserDetailsRepository extends R2dbcRepository<UserDetailsImpl, Long> {

    Mono<UserDetails> findByUsername(String username);

}
