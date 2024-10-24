package com.efedorchenko.timely.security;

import org.springframework.security.core.AuthenticationException;

public class AuthException extends AuthenticationException {

    public AuthException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public AuthException(String message) {
        super(message);
    }

    public AuthException() {
        super("Authentication failed");
    }
}
