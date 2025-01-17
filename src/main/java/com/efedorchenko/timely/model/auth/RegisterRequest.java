package com.efedorchenko.timely.model.auth;

import com.efedorchenko.timely.model.SpaceDto;
import com.efedorchenko.timely.model.validation.Constant;
import com.efedorchenko.timely.model.validation.Email;
import com.efedorchenko.timely.security.model.RoleType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.springframework.lang.Nullable;

@Getter
@ToString
@AllArgsConstructor
//@AtLeastOneNotNull({ "creatingSpace", "spaceKey" })
public class RegisterRequest {

    @Email
    @NotBlank
    private final String username;

    @NotBlank
    @ToString.Exclude
    @Pattern(regexp = Constant.PASSWORD_REGEX)
    private final String password;

    @NotNull
    private final RoleType role;

    @NotNull
    @Size(max = Constant.NAME_OF_USER_MAX_LEN)
    private final String name;

    @NotBlank
    @Size(max = Constant.USER_POSITION_MAX_LEN)
    private final String position;

    @Nullable
    @Positive
    private final Integer rate;

    @Valid
    @Nullable
    private final SpaceDto creatingSpace;

    @Nullable
    @Size(max = Constant.SPACE_KEY_MAX_LEN)
    private final String spaceKey;

}
