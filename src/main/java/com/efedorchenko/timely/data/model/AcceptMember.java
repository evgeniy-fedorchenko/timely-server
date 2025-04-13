package com.efedorchenko.timely.data.model;

import com.efedorchenko.timely.security.model.RoleType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

@Getter
@ToString
@AllArgsConstructor
public class AcceptMember {

    @NotNull
    private final UUID acceptedUserId;

    @NotNull
    private final RoleType newRole;
}
