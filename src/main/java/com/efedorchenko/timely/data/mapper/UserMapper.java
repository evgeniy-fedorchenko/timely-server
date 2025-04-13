package com.efedorchenko.timely.data.mapper;

import com.efedorchenko.timely.data.entity.Role;
import com.efedorchenko.timely.data.entity.Space;
import com.efedorchenko.timely.data.entity.SpaceStatus;
import com.efedorchenko.timely.data.entity.UserDetailsImpl;
import com.efedorchenko.timely.data.entity.UserEntity;
import com.efedorchenko.timely.data.model.SpaceMember;
import com.efedorchenko.timely.data.model.auth.RegisterRequest;
import com.efedorchenko.timely.data.model.auth.UserData;
import com.efedorchenko.timely.data.repository.RoleRepository;
import com.efedorchenko.timely.exception.ExceptionTemplates;
import com.efedorchenko.timely.security.model.RoleType;
import lombok.AllArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@AllArgsConstructor
public class UserMapper {

    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserDetailsImpl toUserDetailsImpl(UUID primaryKey, RegisterRequest registerRequest, @Nullable RoleType roleType) {
        UserDetailsImpl user = new UserDetailsImpl();
        user.setId(primaryKey);

        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        RoleType _roleType = Optional.ofNullable(roleType).orElse(registerRequest.getRole());
        user.setRole(this.getRole(_roleType));
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
        Optional.ofNullable(space).ifPresentOrElse(s -> {
                    user.setConsistsInSpace(s);
                    user.setSpaceStatus(registerRequest.getRole().getPreAcceptSpaceStatus());
                }, () -> user.setSpaceStatus(SpaceStatus.NONE)
        );

        return user;
    }

    public SpaceMember map(UserEntity userEntity, RoleType roleType) {
        return SpaceMember.builder()
                .userId(userEntity.getId())
                .name(userEntity.getName())
                .position(userEntity.getPosition())
                .role(roleType)
                .rate(userEntity.getRate())
                .changedAt(userEntity.getChangedAt())
                .build();
    }

    public UserData map(RegisterRequest request, @Nullable Space space) {
        String spaceName = null;
        SpaceStatus spaceStatus = request.getRole().getPreAcceptSpaceStatus();
        if (space != null) {
            spaceName = space.getName();
        } else if (request.getCreatingSpace() != null) {
            spaceName = request.getCreatingSpace().getName();
        } else {
            spaceStatus = null;
        }
        return new UserData(request.getName(), request.getPosition(), request.getRate(), spaceName, spaceStatus);
    }

    public UserData map(UserEntity entity) {
        Space space = entity.getConsistsInSpace();
        return new UserData(
                entity.getName(),
                entity.getPosition(),
                entity.getRate(),
                space == null ? null : space.getName(),
                entity.getSpaceStatus()
        );
    }

    private Role getRole(RoleType roleType) {
        return roleRepository.findByRoleType(roleType)
                .orElseThrow(() -> ExceptionTemplates.SVR_VAR14.apply(roleType));
    }
}
