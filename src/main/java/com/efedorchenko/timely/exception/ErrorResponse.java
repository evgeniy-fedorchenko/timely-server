package com.efedorchenko.timely.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.annotation.Nullable;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Builder(builderClassName = "Builder")
@JsonInclude(JsonInclude.Include.NON_NULL)
class ErrorResponse {

    private final String exName;

    @Nullable
    private final String sourceExName;

    @lombok.Builder.Default
    private final int errorCode = 1;

    private final String errorMessage;

    @Nullable
    private final String details;

}
