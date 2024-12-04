package com.efedorchenko.timely.model.data;

import com.efedorchenko.timely.model.validation.Constant;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDate;

@Getter
@ToString
@SuperBuilder
@Jacksonized
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FineDto extends UserDataDto {

    static final String TYPE = "fine";

    @Positive
    @Nullable
    private final Long id;

    @Future
    @NotNull
    private final LocalDate date;

    @Nullable
    @Size(max = Constant.FINE_DESCRIPTION_MAX_LEN)
    private final String description;

    @Positive
    private final int amount;

    @Override
    public UserDataType getType() {
        return UserDataType.FINE;
    }

}
