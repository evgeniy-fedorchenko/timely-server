package com.efedorchenko.timely.model;

import com.efedorchenko.timely.entity.SpaceStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.annotation.Nullable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Getter
@ToString
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MembersResponse {

    private final SpaceStatus spaceStatus;

    @Nullable
    private final SpaceDto space;
    private final List<SpaceMember> members;
    private final List<UUID> actualIds;

    public static MembersResponse emptyWith(SpaceStatus spaceStatus) {
        return new MembersResponse(spaceStatus, null, Collections.emptyList(), Collections.emptyList());
    }

    public static MembersResponse of(List<SpaceMember> members, SpaceDto space, List<UUID> actualIds) {
        return new MembersResponse(SpaceStatus.MEMBER, space, members, actualIds);
    }
}
