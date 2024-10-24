package com.efedorchenko.timely.service;

import com.efedorchenko.timely.entity.UserDetailsImpl;
import com.efedorchenko.timely.entity.UserEntity;
import com.efedorchenko.timely.model.AuthRequest;
import com.efedorchenko.timely.model.AuthResponse;
import com.efedorchenko.timely.model.JwtTokenData;
import com.efedorchenko.timely.model.RegisterRequest;
import com.efedorchenko.timely.repository.UserDetailsRepository;
import com.efedorchenko.timely.repository.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final Mapper mapper;
    private final UserEntityRepository userEntityRepository;
    private final UserDetailsRepository userDetailsRepository;

    @Override
    public Mono<AuthResponse> authorise(AuthRequest authRequest) {
        return Mono.empty();
    }

    @Override
    public Mono<JwtTokenData> register(RegisterRequest registerRequest) {

        return userDetailsRepository.findByUsername(registerRequest.getUsername())
                .map(user -> JwtTokenData.empty())
                .switchIfEmpty(Mono.defer(() -> {

                    UUID randomUUID = UUID.randomUUID();
                    JwtTokenData tokenData = new JwtTokenData(randomUUID, registerRequest.getAuthorities(),
                            registerRequest.getName(), registerRequest.getUsername());
                    return Mono.just(tokenData)
                            .flatMap(td -> {
                                Mono.fromRunnable(() -> {
                                            UserDetailsImpl userDetails = mapper.toUserDetailsImpl(registerRequest);
                                            UserEntity userEntity = mapper.toUserEntity(registerRequest);

                                            userDetails.setId(randomUUID);
                                            userEntity.setId(randomUUID);

                                            userEntityRepository.save(userEntity)
                                                    .then(userDetailsRepository.save(userDetails))
                                                    .subscribeOn(Schedulers.boundedElastic())
                                                    .subscribe(
                                                            v -> log.debug("New user saved: {}", v),
                                                            ex -> log.error("Failed to save user data", ex)
                                                    );
                                        })
                                        .subscribeOn(Schedulers.boundedElastic())
                                        .subscribe();

                                return Mono.just(td);
                            });
                }));
    }
}














/*
        return userDetailsRepository.findByUsername(registerRequest.getUsername())
                .map(user -> JwtTokenData.empty())
                .defaultIfEmpty(generateTokenData(registerRequest));
    }

    private JwtTokenData generateTokenData(RegisterRequest registerRequest) {
        return
    }


    private JwtTokenData c() {
        return new JwtTokenData();
    }

    private Mono<JwtTokenData> b() {
        return Mono.just(new JwtTokenData());
    }
}









UUID randomUUID = UUID.randomUUID();
JwtTokenData tokenData = new JwtTokenData(randomUUID, registerRequest.getAuthorities(),
        registerRequest.getName(), registerRequest.getUsername());
                    // Здесь мы создаем и возвращаем Mono<JwtTokenData>
                    return Mono.just(tokenData)
                            .flatMap(td -> {
                                Mono.fromRunnable(() -> {
                                            UserDetailsImpl userDetails = mapper.toUserDetailsImpl(registerRequest);
                                            UserEntity userEntity = mapper.toUserEntity(registerRequest);

                                            userDetails.setId(randomUUID);
                                            userEntity.setId(randomUUID);

                                            userEntityRepository.save(userEntity)
                                                    .then(userDetailsRepository.save(userDetails))
                                                    .subscribeOn(Schedulers.boundedElastic())
                                                    .subscribe(
                                                            v -> log.debug("New user saved: {}", v),
                                                            ex -> log.error("Failed to save user data", ex)
                                                    );
                                        })
                                        .subscribeOn(Schedulers.boundedElastic())
                                        .subscribe();

                                return Mono.just(td);
                            });


                }));

*/