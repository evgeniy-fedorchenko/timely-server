package com.efedorchenko.timely.service;

import com.efedorchenko.timely.entity.Space;
import com.efedorchenko.timely.entity.UserDetailsImpl;
import com.efedorchenko.timely.entity.UserEntity;
import com.efedorchenko.timely.logging.Log;
import com.efedorchenko.timely.mapper.UserMapper;
import com.efedorchenko.timely.model.SpaceKeys;
import com.efedorchenko.timely.model.auth.AuthErrorCode;
import com.efedorchenko.timely.model.auth.AuthResponse;
import com.efedorchenko.timely.model.auth.RegisterRequest;
import com.efedorchenko.timely.repository.UserDetailsRepository;
import com.efedorchenko.timely.repository.UserEntityRepository;
import com.efedorchenko.timely.security.JwtUtil;
import com.efedorchenko.timely.security.model.AuthData;
import com.efedorchenko.timely.security.model.JwtTokenData;
import com.efedorchenko.timely.security.model.RoleType;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public AuthResponse register(RegisterRequest request) throws RuntimeException {
        return userDetailsRepository.findByUsername(request.getUsername())
                .map(ignored -> AuthResponse.error(AuthErrorCode.ALREADY_REGISTERED))
                .orElseGet(() -> createUser(request));
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(UUID userId) {
        return userDetailsRepository.findById(userId)
                .map(this::loadUser)
                .orElse(AuthResponse.error(AuthErrorCode.UNREGISTERED)); // Обычно Security бракует запрос еще у себя
    }

    @Override
    public void logout() {
        // TODO 02.11.2024 22:49: реализовать logout
    }

    private AuthResponse createUser(RegisterRequest request) {
        String spaceKey = request.getSpaceKey();
        Space findedSpace = spaceService.findSpace(spaceKey, request.getRole());
        if (spaceKey != null && findedSpace == null) {
            return AuthResponse.error(AuthErrorCode.SPACE_NOT_FOUND);
        }

        RoleType initRole = request.getRole().doPreAccept();
        UUID primaryKey = UUID.randomUUID();
        UserDetailsImpl userDetails = userMapper.toUserDetailsImpl(primaryKey, request, initRole);
        AuthData.Builder authDataBuilder = AuthData.builder()
                .userId(primaryKey)
                .jwtToken(jwtUtil.generateToken(JwtTokenData.fromDetails(userDetails)))
                .role(initRole);

        SpaceKeys detachedKeys;
        boolean needCreateSpace;
        if (request.getCreatingSpace() != null) {
            if (request.getRole() != RoleType.CREATOR) {
                return AuthResponse.error(AuthErrorCode.SPACE_CREATION_PROHIBITED);
            }
            needCreateSpace = true;
            detachedKeys = spaceService.createDetachedKeys();
            authDataBuilder.generatedSpaceKeys(detachedKeys);
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

        return AuthResponse.builder()
                .userData(userMapper.map(request, findedSpace))
                .authData(authDataBuilder.build())
                .build();
    }

    private AuthResponse loadUser(UserDetailsImpl userDetails) {
        UUID userId = userDetails.getId();
        UserEntity userEntity = userEntityRepository.findById(userId).orElseThrow(); // have equals id
        RoleType roleType = userDetails.getRole().getRoleType();

        AuthData.Builder authDataBuilder = AuthData.builder()
                .userId(userId)
                .jwtToken(jwtUtil.generateToken(JwtTokenData.fromDetails(userDetails)))
                .role(roleType);

        if (roleType.spaceOpsAccess()) {
            authDataBuilder.generatedSpaceKeys(spaceService.getKeys(userId));
        }
        return AuthResponse.builder()
                .isRegister(true)
                .userData(userMapper.map(userEntity))
                .authData(authDataBuilder.build())
                .build();
    }
}
