package com.efedorchenko.timely.model.auth;

import com.efedorchenko.timely.model.validation.Constant;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class AuthRequest {

    @Email
    @NotBlank
    private final String username;

    @NotBlank
    @ToString.Exclude
    @Pattern(regexp = Constant.PASSWORD_REGEX)
    private final String password;

}
