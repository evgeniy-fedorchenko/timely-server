package com.efedorchenko.timely.model.data;

import com.efedorchenko.timely.entity.EventStatus;
import com.efedorchenko.timely.model.validation.Constant;
import com.efedorchenko.timely.model.validation.ValidDuration;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.time.Duration;

@Getter
@ToString(callSuper = true)
@SuperBuilder
@Jacksonized
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EventDto extends UserDataDto {

    static final String TYPE = "event";

    @NotNull
    @ValidDuration
    private final Duration workDuration;

    @Nullable
    @Size(max = Constant.COMMENT_MAX_LEN)
    private final String comment;

    @Nullable
    private final EventStatus status;
// TODO 10.04.2025 00:27: owner
    @Override
    public UserDataType getType() {
        return UserDataType.EVENT;
    }
}
