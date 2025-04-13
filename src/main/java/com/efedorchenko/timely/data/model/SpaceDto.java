package com.efedorchenko.timely.data.model;

import com.efedorchenko.timely.data.validation.Constant;
import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor(onConstructor_ = @JsonCreator)
public class SpaceDto {

    @NotBlank
    @Size(max = Constant.SPACE_NAME_MAX_LEN)
    private final String name;

}
