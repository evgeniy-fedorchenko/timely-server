package com.efedorchenko.timely.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.Collections;
import java.util.List;

@Getter
@ToString
@AllArgsConstructor
public class MembersResult {

    private final boolean consistInSpace;
    private final List<SpaceMember> members;

    public static MembersResult notConsist() {
        return new MembersResult(false, Collections.emptyList());
    }

    public static MembersResult withMembers(List<SpaceMember> members) {
        return new MembersResult(true, members);
    }
}
