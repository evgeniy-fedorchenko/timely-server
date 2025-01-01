package com.efedorchenko.timely.model.auth;

import com.efedorchenko.timely.model.validation.Constant;
import com.efedorchenko.timely.model.validation.Email;
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
