package com.efedorchenko.timely.data.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class MemberOpResult {

    private final MemberOpResultType result;

    public static MemberOpResult success() {
        return new MemberOpResult(MemberOpResultType.SUCCESS);
    }
    public static MemberOpResult fail() {
        return new MemberOpResult(MemberOpResultType.FAIL);
    }
    public static MemberOpResult alreadyProcessed() {
        return new MemberOpResult(MemberOpResultType.ALREADY_PROCESSED);
    }
    public static MemberOpResult alreadyCanceled() {
        return new MemberOpResult(MemberOpResultType.ALREADY_CANCELED);
    }

    public enum MemberOpResultType {
        SUCCESS,
        FAIL,
        ALREADY_PROCESSED,
        ALREADY_CANCELED
    }
}
