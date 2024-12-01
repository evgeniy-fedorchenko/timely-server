package com.efedorchenko.timely.mapper;

import com.efedorchenko.timely.entity.Role;
import com.efedorchenko.timely.entity.Space;
import com.efedorchenko.timely.entity.UserDetailsImpl;
import com.efedorchenko.timely.entity.UserEntity;
import com.efedorchenko.timely.exception.ServerException;
import com.efedorchenko.timely.model.auth.RegisterRequest;
import com.efedorchenko.timely.model.auth.RoleType;
import com.efedorchenko.timely.repository.RoleRepository;
import lombok.AllArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;

@Component
@AllArgsConstructor
public class UserMapper {

    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserDetailsImpl toUserDetailsImpl(UUID primaryKey, RegisterRequest registerRequest) {
        UserDetailsImpl user = new UserDetailsImpl();
        user.setId(primaryKey);

        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        Set<Role> roles = Set.of(this.getRole(registerRequest.getRole()));
        user.setRoles(roles);

        return user;
    }

    public UserEntity toUserEntity(UUID primaryKey, RegisterRequest registerRequest) {
        return this.toUserEntity(primaryKey, registerRequest, null);
    }

    public UserEntity toUserEntity(UUID primaryKey, RegisterRequest registerRequest, @Nullable Space space) {
        UserEntity user = new UserEntity();

        user.setId(primaryKey);
        user.setName(registerRequest.getName());
        user.setPosition(registerRequest.getPosition());
        user.setRate(registerRequest.getRate());
        user.setConsistsInSpace(space);

        return user;
    }

    private Role getRole(RoleType roleType) {
        return roleRepository.findByValue(roleType).orElseThrow(() -> {
            String errMess = "Role [%s] does not exist. There are only [%s]. Please check how validation allowed this type"
                    .formatted(roleType, roleRepository.getValues());
            return new ServerException(errMess);
        });
    }
}
