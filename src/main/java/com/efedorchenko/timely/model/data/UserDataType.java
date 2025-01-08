package com.efedorchenko.timely.model.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collection;
import java.util.List;

@Getter
@AllArgsConstructor
public enum UserDataType {

    EVENT(false),
    FINE(true),
    PARENT(false);

    /** Отвечает на вопрос "Можно ли иметь несколько объектов на одну дату" */
    private final boolean isRepeatable;

    public static Collection<UserDataType> getConcreteTypes() {
        return List.of(EVENT, FINE);
    }
}
