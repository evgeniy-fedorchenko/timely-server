package com.efedorchenko.timely.service;

import com.efedorchenko.timely.model.AuthRequest;
import com.efedorchenko.timely.model.AuthResponse;
import com.efedorchenko.timely.model.AuthStatus;
import com.efedorchenko.timely.model.Role;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.UUID;

@Service
public class AuthService {

    private static final AuthResponse failResponse = new AuthResponse(AuthStatus.FAIL, null, null);

    private static final Map<AuthRequest, AuthResponse> users = Map.of(
            new AuthRequest("user1", "pass-user1"),       new AuthResponse(AuthStatus.SUCCESS, Role.WORKER,  UUID.fromString("094bcf64-32a5-4701-97f9-4f8b3423624f")),
            new AuthRequest("user2", "pass-user2"),       new AuthResponse(AuthStatus.SUCCESS, Role.WORKER,  UUID.fromString("933f2088-dd21-40f0-8760-de9936103d48")),
            new AuthRequest("user3", "pass-user3"),       new AuthResponse(AuthStatus.SUCCESS, Role.WORKER,  UUID.fromString("fb7bc508-b497-4e90-b907-7a59ad7f129f")),
            new AuthRequest("boss1", "pass-boss1"),       new AuthResponse(AuthStatus.SUCCESS, Role.BOSS,    UUID.fromString("b74a5111-ae09-4bcc-ac2a-b2c0ecdee898")),
            new AuthRequest("boss2", "pass-boss2"),       new AuthResponse(AuthStatus.SUCCESS, Role.BOSS,    UUID.fromString("bd320d0c-e8b5-4e8f-ada1-c76c473c91d3")),
            new AuthRequest("creator1", "pass-creator1"), new AuthResponse(AuthStatus.SUCCESS, Role.CREATOR, UUID.fromString("87c83e21-1267-4467-86fe-2a69d1d6f87f"))
    );


    public Mono<AuthResponse> authorise(AuthRequest authRequest) {
        return Mono.just(users.getOrDefault(authRequest, failResponse));
    }
}
