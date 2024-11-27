package com.efedorchenko.timely.model.data;

import com.efedorchenko.timely.model.validation.Constant;
import com.efedorchenko.timely.model.validation.ValidDuration;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

import java.time.Duration;
import java.time.LocalDate;

@Getter
@ToString
@Builder
@Jacksonized
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EventDto extends UserDataDto {

    static final String TYPE = "event";

    @Positive
    @Nullable
    private final Long id;

    @Future
    @NotNull
    private final LocalDate date;

    @NotNull
    @ValidDuration
    private final Duration workDuration;

    @Nullable
    @Size(max = Constant.COMMENT_MAX_LEN)
    private final String comment;

    @Override
    public UserDataType getType() {
        return UserDataType.EVENT;
    }
}
