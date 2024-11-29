package com.efedorchenko.timely.model.data;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

import java.util.UUID;

@Getter
@Builder
@Jacksonized
@ToString
public class UserDataModifyDto {

    @NotNull
    private final UUID modifyingUserId;

    @NotNull
    private final UserDataDto newData;

}
