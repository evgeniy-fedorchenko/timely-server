package com.efedorchenko.timely.model.auth;

import com.efedorchenko.timely.entity.UserEntity;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class UserData {

    private final String name;

    private final String position;

    @Nullable
    private final Integer rate;

    private final String spaceName;

    public static UserData fromRequest(RegisterRequest request, String spaceName) {
        return new UserData(request.getName(), request.getPosition(), request.getRate(), spaceName);
    }

    public static UserData fromEntity(UserEntity entity) {
        return new UserData(
                entity.getName(),
                entity.getPosition(),
                entity.getRate(),
                entity.getConsistsInSpace().getName()
        );
    }
}
