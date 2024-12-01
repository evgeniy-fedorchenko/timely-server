package com.efedorchenko.timely.exception;

import lombok.Getter;
import org.springframework.validation.FieldError;

@Getter
class ArgInvalidDetails {

    private final String field;

    private final Object rejectedValue;

    private final String details;

    ArgInvalidDetails(FieldError fieldError) {
        this.field = fieldError.getField();
        this.rejectedValue = fieldError.getRejectedValue();
        this.details = fieldError.getDefaultMessage();
    }
}
