package com.efedorchenko.timely.entity;

import com.efedorchenko.timely.model.UserDataType;

import java.beans.Transient;
import java.time.LocalDate;

public abstract class UserData {

    public abstract LocalDate getDate();

    @Transient
    public abstract UserDataType getType();
}
