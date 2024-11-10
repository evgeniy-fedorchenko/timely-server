package com.efedorchenko.timely.entity;

import com.efedorchenko.timely.model.UserDataType;
import com.efedorchenko.timely.model.validation.ValidDuration;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.time.Duration;
import java.time.LocalDate;

@Getter
@ToString
@AllArgsConstructor(onConstructor_ = @JsonCreator)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Event extends UserData {

    private static final int COMMENT_MAX_SIZE = 500;

    @Future
    @NotNull
    private final LocalDate date;

    @NotNull
    @ValidDuration
    private final Duration workDuration;

    @Nullable
    @Size(max = COMMENT_MAX_SIZE)
    private final String comment;

    @Override
    public UserDataType getType() {
        return UserDataType.EVENT;
    }

}
