package com.efedorchenko.timely.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
    private final List<ArgInvalidDetails> details;

    static class Builder {
        Builder details(List<ArgInvalidDetails> details) {
            this.details = List.copyOf(details);
            return this;
        }
    }

    @NotNull
    public List<ArgInvalidDetails> getDetails() {
        return details == null ? Collections.emptyList() : new ArrayList<>(details);
    }
}
