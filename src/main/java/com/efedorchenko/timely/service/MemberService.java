package com.efedorchenko.timely.service;

import com.efedorchenko.timely.model.SpaceMember;

import java.util.List;
import java.util.UUID;

public interface MemberService {

    List<SpaceMember> getMembers(UUID userId);

    boolean leaveSpace(UUID userId);

    boolean kickedUser(UUID userId, UUID kickedUserId);
}
