package com.efedorchenko.timely.data.model;

import com.efedorchenko.timely.data.entity.SpaceStatus;
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

    @Nullable
    private final SpaceDto spaceDto;

    public static SpaceConnectResponse keyInvalid() {
        return new SpaceConnectResponse(SpaceConnectResultType.KEY_INVALID, null, null);
    }

    public static SpaceConnectResponse success(SpaceStatus newSpaceStatus, SpaceDto spaceDto) {
        return new SpaceConnectResponse(SpaceConnectResultType.SUCCESS,  newSpaceStatus, spaceDto);
    }
}
