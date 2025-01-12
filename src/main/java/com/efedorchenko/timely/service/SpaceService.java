package com.efedorchenko.timely.service;

import com.efedorchenko.timely.entity.Space;
import com.efedorchenko.timely.model.GetMembersResponse;
import com.efedorchenko.timely.model.SpaceConnectResponse;
import com.efedorchenko.timely.model.SpaceDto;
import com.efedorchenko.timely.model.SpaceKeys;
import com.efedorchenko.timely.security.model.RoleType;
import org.springframework.lang.Nullable;

import java.time.Instant;
import java.util.UUID;

public interface SpaceService {

    SpaceKeys create(UUID userId, SpaceDto spaceDto, SpaceKeys spaceKeys);

    @Nullable
    Space findSpace(String spaceKey, RoleType roleType);

    @Nullable
    SpaceKeys getKeys(UUID userId);

    SpaceKeys createDetachedKeys();

    GetMembersResponse getMembers(UUID userId, @Nullable Instant since);

    boolean leaveSpace(UUID userId);

    boolean detachUser(UUID targetUserId);

    SpaceConnectResponse connectToSpace(UUID userId, String spaceKey);
}
