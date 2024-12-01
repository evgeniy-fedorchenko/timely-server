package com.efedorchenko.timely.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    BUSINESS(1),
    SERVER(2),
    VALIDATION(3),
    UNKNOWN(4);

    private final int rawCode;

}
