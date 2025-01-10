package com.efedorchenko.timely.mapper;

import com.efedorchenko.timely.entity.Role;
import com.efedorchenko.timely.entity.Space;
import com.efedorchenko.timely.entity.UserDetailsImpl;
import com.efedorchenko.timely.entity.UserEntity;
import com.efedorchenko.timely.exception.ExceptionTemplates;
import com.efedorchenko.timely.model.SpaceMember;
import com.efedorchenko.timely.model.auth.RegisterRequest;
import com.efedorchenko.timely.repository.RoleRepository;
import com.efedorchenko.timely.security.model.RoleType;
import lombok.AllArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

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
        user.setRole(this.getRole(registerRequest.getRole()));

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

    public SpaceMember map(UserEntity userEntity) {
        return new SpaceMember(
                userEntity.getId(),
                userEntity.getName(),
                userEntity.getPosition()
        );
    }

    private Role getRole(RoleType roleType) {
        return roleRepository.findByRoleType(roleType)
                .orElseThrow(() -> ExceptionTemplates.SVR_VAR14.apply(roleType));
    }
}
