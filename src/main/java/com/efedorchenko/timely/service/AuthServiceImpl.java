package com.efedorchenko.timely.service;

import com.efedorchenko.timely.entity.UserDetailsImpl;
import com.efedorchenko.timely.entity.UserEntity;
import com.efedorchenko.timely.logging.Log;
import com.efedorchenko.timely.model.auth.AuthFailReason;
import com.efedorchenko.timely.model.auth.JwtTokenData;
import com.efedorchenko.timely.model.auth.RegisterRequest;
import com.efedorchenko.timely.repository.UserDetailsRepository;
import com.efedorchenko.timely.repository.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.event.Level;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final Mapper mapper;
    private final ExecutorService executorOfVirtual;
    private final UserEntityRepository userEntityRepository;
    private final UserDetailsRepository userDetailsRepository;

    @Log(level = Level.TRACE)
    @Override
    public JwtTokenData register(RegisterRequest registerRequest) {

        return userDetailsRepository.findByUsername(registerRequest.getUsername())
                .map(ignored -> JwtTokenData.failWith(AuthFailReason.ALREADY_REGISTERED))
                .orElseGet(() -> {

                    UUID randomUUID = UUID.randomUUID();
                    UserDetailsImpl userDetails = mapper.toUserDetailsImpl(registerRequest);
                    userDetails.setId(randomUUID);

                    JwtTokenData jwtTokenData = JwtTokenData.fromDetails(userDetails);
                    CompletableFuture.runAsync(() -> {
                        UserEntity userEntity = mapper.toUserEntity(registerRequest);
                        userEntity.setId(randomUUID);

                        UserEntity user = userEntityRepository.save(userEntity);
                        UserDetailsImpl details = userDetailsRepository.save(userDetails);
                        log.debug("New user saved. User: {}. Details: {}", user, details);

                    }, executorOfVirtual);

                    return jwtTokenData;
                });
    }

    @Log(level = Level.TRACE)
    @Override
    public JwtTokenData login(UUID userId) {
//        Ошибшихся в логине/пароле отсеет Spring Security
        return userDetailsRepository.findById(userId)
                .map(JwtTokenData::fromDetails)
                .orElseThrow();
    }

    @Log(level = Level.TRACE)
    @Override
    public void logout() {
    }

}
