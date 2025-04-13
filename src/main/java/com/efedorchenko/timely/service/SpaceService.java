package com.efedorchenko.timely.service;

import com.efedorchenko.timely.data.entity.Space;
import com.efedorchenko.timely.data.model.AcceptMember;
import com.efedorchenko.timely.data.model.MemberOpResult;
import com.efedorchenko.timely.data.model.MembersResponse;
import com.efedorchenko.timely.data.model.SpaceConnectResponse;
import com.efedorchenko.timely.data.model.SpaceDto;
import com.efedorchenko.timely.data.model.SpaceKeys;
import com.efedorchenko.timely.security.model.RoleType;

import java.time.Instant;
import java.util.UUID;

public interface SpaceService {

    SpaceKeys create(UUID userId, SpaceDto spaceDto, SpaceKeys spaceKeys);

    Space findSpace(String spaceKey, RoleType roleType);

    SpaceKeys getKeys(UUID userId);

    SpaceKeys createDetachedKeys();

    MembersResponse getMembers(UUID userId, Instant since, boolean withJoinRequests);

    boolean leaveSpace(UUID userId);

    boolean detachUser(UUID targetUserId, UUID kickedUserId);

    SpaceConnectResponse requestConnectToSpace(UUID userId, String spaceKey);

    MemberOpResult acceptMember(UUID userId, AcceptMember acceptMember);
}
