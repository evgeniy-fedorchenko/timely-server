package com.efedorchenko.timely.service;

import com.efedorchenko.timely.entity.Role;
import com.efedorchenko.timely.entity.Space;
import com.efedorchenko.timely.entity.UserDetailsImpl;
import com.efedorchenko.timely.entity.UserEntity;
import com.efedorchenko.timely.logging.Log;
import com.efedorchenko.timely.mapper.UserMapper;
import com.efedorchenko.timely.model.SpaceKeys;
import com.efedorchenko.timely.model.auth.AuthFailReason;
import com.efedorchenko.timely.model.auth.AuthResponse;
import com.efedorchenko.timely.model.auth.JwtTokenData;
import com.efedorchenko.timely.model.auth.RegisterRequest;
import com.efedorchenko.timely.model.auth.RoleType;
import com.efedorchenko.timely.repository.UserDetailsRepository;
import com.efedorchenko.timely.repository.UserEntityRepository;
import com.efedorchenko.timely.security.JwtUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

@Log
@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService<RegisterRequest, AuthResponse> {

    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;
    private final SpaceService spaceService;
    private final ExecutorService executorOfVirtual;
    private final UserEntityRepository userEntityRepository;
    private final UserDetailsRepository userDetailsRepository;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        return userDetailsRepository.findByUsername(request.getUsername())
                .map(ignored -> AuthResponse.failWith(AuthFailReason.ALREADY_REGISTERED))
                .orElseGet(() -> {

                    String spaceKey = request.getSpaceKey();
                    Space findedSpace = spaceService.findSpace(spaceKey, request.getRole());
                    if (spaceKey != null && findedSpace == null) {
                        return AuthResponse.failWith(AuthFailReason.SPACE_NOT_FOUND);
                    }
                    UUID primaryKey = UUID.randomUUID();
                    UserDetailsImpl userDetails = userMapper.toUserDetailsImpl(primaryKey, request);

                    JwtTokenData jwtTokenData = JwtTokenData.fromDetails(userDetails);
                    AuthResponse.Builder responseBuilder = AuthResponse.builder()
                            .isRegister(true)
                            .jwtToken(jwtUtil.generateToken(jwtTokenData))
                            .roles(Collections.singleton(request.getRole()));

                    SpaceKeys detachedKeys;
                    boolean needCreateSpace;
                    if (request.getCreatingSpace() != null) {
                        if (request.getRole() != RoleType.CREATOR) {
                            return AuthResponse.failWith(AuthFailReason.SPACE_CREATION_PROHIBITED);
                        }
                        needCreateSpace = true;
                        detachedKeys = spaceService.createDetachedKeys();
                        responseBuilder.generatedSpaceKeys(detachedKeys);
                    } else {
                        detachedKeys = null;
                        needCreateSpace = false;
                    }

                    CompletableFuture.runAsync(() -> {
                        UserEntity userEntity = userMapper.toUserEntity(primaryKey, request, findedSpace);
                        userEntityRepository.save(userEntity);
                        userDetailsRepository.save(userDetails);
                        if (needCreateSpace) {
                            spaceService.create(primaryKey, request.getCreatingSpace(), detachedKeys);
                        }
                    }, executorOfVirtual);

                    return responseBuilder.build();
                });
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(UUID userId) {
        Optional<UserDetailsImpl> userDetailsOpt = userDetailsRepository.findById(userId);
        if (userDetailsOpt.isEmpty()) {
            return AuthResponse.failWith(AuthFailReason.UNREGISTERED);
        }
        UserDetailsImpl userDetails = userDetailsOpt.get();
        JwtTokenData jwtTokenData = JwtTokenData.fromDetails(userDetails);

        Set<RoleType> roleTypes = userDetails.getRoles().stream()
                .map(Role::getValue)
                .collect(Collectors.toSet());

        AuthResponse.Builder responseBuilder = AuthResponse.builder()
                .isRegister(true)
                .jwtToken(jwtUtil.generateToken(jwtTokenData))
                .roles(roleTypes);

        if (RoleType.getMax(roleTypes).spaceOpsAccess()) {
            responseBuilder.generatedSpaceKeys(spaceService.getKeys(userId));
        }
        return responseBuilder.build();
    }

    @Override
    public void logout() {
        // TODO 02.11.2024 22:49: реализовать logout
    }

}
