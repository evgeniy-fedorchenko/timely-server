package com.efedorchenko.timely.service;

import com.efedorchenko.timely.entity.Space;
import com.efedorchenko.timely.entity.UserDetailsImpl;
import com.efedorchenko.timely.entity.UserEntity;
import com.efedorchenko.timely.logging.Log;
import com.efedorchenko.timely.mapper.UserMapper;
import com.efedorchenko.timely.model.SpaceKeys;
import com.efedorchenko.timely.model.auth.AuthErrorCode;
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

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

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
                .map(ignored -> AuthResponse.failWith(AuthErrorCode.ALREADY_REGISTERED))
                .orElseGet(() -> {

                    String spaceKey = request.getSpaceKey();
                    Space findedSpace = spaceService.findSpace(spaceKey, request.getRole());
                    if (spaceKey != null && findedSpace == null) {
                        return AuthResponse.failWith(AuthErrorCode.SPACE_NOT_FOUND);
                    }
                    UUID primaryKey = UUID.randomUUID();
                    UserDetailsImpl userDetails = userMapper.toUserDetailsImpl(primaryKey, request);

                    JwtTokenData jwtTokenData = JwtTokenData.fromDetails(userDetails);
                    AuthResponse.Builder responseBuilder = AuthResponse.builder()
                            .userId(primaryKey)
                            .isRegister(true)
                            .jwtToken(jwtUtil.generateToken(jwtTokenData))
                            .role(request.getRole());

                    SpaceKeys detachedKeys;
                    boolean needCreateSpace;
                    if (request.getCreatingSpace() != null) {
                        if (request.getRole() != RoleType.CREATOR) {
                            return AuthResponse.failWith(AuthErrorCode.SPACE_CREATION_PROHIBITED);
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
            return AuthResponse.failWith(AuthErrorCode.UNREGISTERED);
        }
        UserDetailsImpl userDetails = userDetailsOpt.get();
        JwtTokenData jwtTokenData = JwtTokenData.fromDetails(userDetails);

        RoleType roleType = userDetails.getRole().getValue();
        AuthResponse.Builder responseBuilder = AuthResponse.builder()
                .userId(userDetails.getId())
                .isRegister(true)
                .jwtToken(jwtUtil.generateToken(jwtTokenData))
                .role(roleType);

        if (roleType.spaceOpsAccess()) {
            responseBuilder.generatedSpaceKeys(spaceService.getKeys(userId));
        }
        return responseBuilder.build();
    }

    @Override
    public void logout() {
        // TODO 02.11.2024 22:49: реализовать logout
    }

}
