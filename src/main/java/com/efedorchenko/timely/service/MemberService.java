package com.efedorchenko.timely.service;

import com.efedorchenko.timely.model.MembersResult;
import jakarta.annotation.Nullable;

import java.time.Instant;
import java.util.UUID;

public interface MemberService {

    MembersResult getMembers(UUID userId, @Nullable Instant since);

    boolean leaveSpace(UUID userId);

    boolean detachUser(UUID userId, UUID kickedUserId);
}
