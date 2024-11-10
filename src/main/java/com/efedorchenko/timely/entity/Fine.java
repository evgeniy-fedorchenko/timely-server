package com.efedorchenko.timely.entity;

import com.efedorchenko.timely.model.UserDataType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@ToString
@AllArgsConstructor(onConstructor_ = @JsonCreator)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Fine extends UserData {

    @Future
    @NotNull
    private final LocalDate date;

    @Nullable
    private final String description;

    @Positive
    private final int amount;

    @Override
    public UserDataType getType() {
        return UserDataType.FINE;
    }

}
