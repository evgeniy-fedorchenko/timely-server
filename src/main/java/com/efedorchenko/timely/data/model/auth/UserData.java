package com.efedorchenko.timely.data.model.auth;

import com.efedorchenko.timely.data.entity.SpaceStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserData {

    private final String name;

    private final String position;

    @Nullable
    private final Integer rate;

    @Nullable
    private final String spaceName;

    @Nullable
    private final SpaceStatus spaceStatus;
}
