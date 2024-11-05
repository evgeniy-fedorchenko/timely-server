package com.efedorchenko.timely.service;

import com.efedorchenko.timely.entity.UserDetailsImpl;
import com.efedorchenko.timely.entity.UserEntity;
import com.efedorchenko.timely.logging.Log;
import com.efedorchenko.timely.model.auth.JwtTokenData;
import com.efedorchenko.timely.model.auth.RegisterRequest;
import com.efedorchenko.timely.repository.UserDetailsRepository;
import com.efedorchenko.timely.repository.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final Mapper mapper;
    private final UserEntityRepository userEntityRepository;
    private final UserDetailsRepository userDetailsRepository;

    @Log
    @Override
    public Mono<JwtTokenData> register(RegisterRequest registerRequest) {

        return userDetailsRepository.findByUsername(registerRequest.getUsername())
                .map(user -> JwtTokenData.empty())
                .switchIfEmpty(Mono.defer(() -> {

                    UUID randomUUID = UUID.randomUUID();
                    UserDetailsImpl userDetails = mapper.toUserDetailsImpl(registerRequest);
                    userDetails.setId(randomUUID);

                    return Mono.just(JwtTokenData.fromDetails(userDetails))
                            .flatMap(jwtTokenData -> {
                                Mono.fromRunnable(() -> {
                                            UserEntity userEntity = mapper.toUserEntity(registerRequest);
                                            userEntity.setId(randomUUID);
                                            userEntityRepository.save(userEntity)
                                                    .then(userDetailsRepository.save(userDetails))
                                                    .subscribeOn(Schedulers.boundedElastic())
                                                    .subscribe(
                                                            v -> log.debug("New user saved: {}", v),
                                                            ex -> log.error("Failed to save user. Ex: ", ex)
                                                    );
                                        })
                                        .subscribeOn(Schedulers.boundedElastic())
                                        .subscribe();

                                return Mono.just(jwtTokenData);
                            });
                }));
    }

    @Log
    @Override
    public Mono<JwtTokenData> login(UUID userId) {
        return userDetailsRepository.findById(userId).map(JwtTokenData::fromDetails);
    }

    @Log
    @Override
    public Mono<Void> logout() {
        return null;
    }

}
