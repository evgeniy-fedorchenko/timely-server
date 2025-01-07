package com.efedorchenko.timely.model.data;

import com.efedorchenko.timely.model.validation.Constant;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

@Getter
@ToString
@SuperBuilder
@Jacksonized
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FineDto extends UserDataDto {

    static final String TYPE = "fine";

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
