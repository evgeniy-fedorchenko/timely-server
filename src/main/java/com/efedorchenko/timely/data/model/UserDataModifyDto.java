package com.efedorchenko.timely.data.model;

import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

import java.util.UUID;

@Getter
@ToString
@Builder
@Jacksonized
public class UserDataModifyDto {

    @Nullable
    private final UUID modifyingUserId;

    @Valid
    @NotNull
    private final UserDataDto newData;

}
