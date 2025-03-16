package com.efedorchenko.timely.model;

import com.efedorchenko.timely.entity.SpaceStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.annotation.Nullable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SpaceConnectResponse {

    private final SpaceConnectResultType result;

    @Nullable
    private final SpaceStatus newSpaceStatus;

    public static SpaceConnectResponse keyInvalid() {
        return new SpaceConnectResponse(SpaceConnectResultType.KEY_INVALID, null);
    }

    public static SpaceConnectResponse success(SpaceStatus newSpaceStatus) {
        return new SpaceConnectResponse(SpaceConnectResultType.SUCCESS,  newSpaceStatus);
    }
}
