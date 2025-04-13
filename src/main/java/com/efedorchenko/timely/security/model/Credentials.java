package com.efedorchenko.timely.security.model;

import com.efedorchenko.timely.data.validation.Constant;
import com.efedorchenko.timely.data.validation.Email;
import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor(onConstructor_ = @JsonCreator)
public class Credentials {

    @Email
    @NotBlank
    private final String username;

    @NotBlank
    @ToString.Exclude
    @Pattern(regexp = Constant.PASSWORD_REGEX)
    private final String password;

}
