package com.efedorchenko.timely.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    INVALID_DATA(1000),

    REQUIRES_NOT_NULL(2000),

    RESOURCE_NOT_FOUND(3000),
    NOT_FOUND_FOR_SERVER_EX(3001),
    NOT_FOUND_FOR_BUSINESS_EX(3002),

    VALIDATION(4000),

    CONFIGURATION(5000),

    AUTH(6000),
    FORBIDDEN(7001),

    UNKNOWN(8000);

    private final int rawCode;

}
