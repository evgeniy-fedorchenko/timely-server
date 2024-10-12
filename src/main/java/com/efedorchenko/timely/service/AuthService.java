package com.efedorchenko.timely.service;

import com.efedorchenko.timely.model.AuthRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.UUID;

@Service
public class AuthService {

    private static final Map<AuthRequest, String> users = Map.of(
            new AuthRequest("user1", "pass-user1"), "094bcf64-32a5-4701-97f9-4f8b3423624f",
            new AuthRequest("user2", "pass-user2"), "933f2088-dd21-40f0-8760-de9936103d48",
            new AuthRequest("user3", "pass-user3"), "fb7bc508-b497-4e90-b907-7a59ad7f129f",
            new AuthRequest("boss1", "pass-boss1"), "b74a5111-ae09-4bcc-ac2a-b2c0ecdee898"
    );

    public Mono<UUID> authorise(AuthRequest authRequest) {
        return Mono.justOrEmpty(UUID.fromString(users.get(authRequest)));
    }
}
