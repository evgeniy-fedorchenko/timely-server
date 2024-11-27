package com.efedorchenko.timely.model.data;

import java.util.Collection;
import java.util.List;

public enum UserDataType {

    EVENT, FINE, PARENT;

    public static Collection<UserDataType> getConcreteTypes() {
        return List.of(EVENT, FINE);
    }
}
