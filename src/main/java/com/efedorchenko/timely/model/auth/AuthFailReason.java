package com.efedorchenko.timely.model.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AuthFailReason {

    ALREADY_REGISTERED(HttpStatus.CONFLICT, "User already registered (username already exists)"),
    UNREGISTERED(HttpStatus.UNAUTHORIZED, "Unknown user"),
    SPACE_NOT_FOUND(HttpStatus.NOT_FOUND, "The specified space was not found"),
    SPACE_CREATION_PROHIBITED(HttpStatus.FORBIDDEN, "Creation of a spaces is prohibited for your role");

    private final HttpStatus httpStatus;
    private final String description;
}
