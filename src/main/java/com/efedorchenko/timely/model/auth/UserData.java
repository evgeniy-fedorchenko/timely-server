package com.efedorchenko.timely.model.auth;

import com.efedorchenko.timely.entity.Space;
import com.efedorchenko.timely.entity.UserEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserData {

    private final String name;

    private final String position;

    @Nullable
    private final Integer rate;

    @Nullable
    private final String spaceName;

    public static UserData fromRequest(RegisterRequest request, @Nullable Space space) {
        String spaceName = null;
        if (space != null) {
            spaceName = space.getName();
        } else if (request.getCreatingSpace() != null) {
            spaceName = request.getCreatingSpace().getName();
        }
        return new UserData(request.getName(), request.getPosition(), request.getRate(), spaceName);
    }

    public static UserData fromEntity(UserEntity entity) {
        Space space = entity.getConsistsInSpace();
        return new UserData(
                entity.getName(),
                entity.getPosition(),
                entity.getRate(),
                space == null ? null : space.getName()
        );
    }
}
