package com.efedorchenko.timely.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    private final String exName;

    @Builder.Default
    private final int errorCode = 1;

    private final String errorMessage;

}
