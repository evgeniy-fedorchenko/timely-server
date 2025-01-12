package com.efedorchenko.timely.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.Collections;
import java.util.List;

@Getter
@ToString
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class GetMembersResponse {

    private final boolean youConsistInSpace;
    private final List<SpaceMember> members;

    public static GetMembersResponse youNotConsist() {
        return new GetMembersResponse(false, Collections.emptyList());
    }

    public static GetMembersResponse with(List<SpaceMember> members) {
        return new GetMembersResponse(true, members);
    }
}
