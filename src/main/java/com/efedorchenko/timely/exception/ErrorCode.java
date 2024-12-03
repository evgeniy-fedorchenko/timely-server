package com.efedorchenko.timely.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    BUSINESS(1000),
    INVALID_DATA(1001),
    FORBIDDEN(1002),
    REQUIRES_NOT_NULL(1003),
    RESOURCE_NOT_FOUND(1004),

    SERVER(2000),
    VALIDATION(3000),
    AUTH(4000),
    UNKNOWN(5000);

    private final int rawCode;

}
