package com.efedorchenko.timely.service;

import java.util.UUID;

public interface AuthService<REQ, RESP> {

    RESP register(REQ request);

    RESP login(UUID userId);

    void logout();
}
