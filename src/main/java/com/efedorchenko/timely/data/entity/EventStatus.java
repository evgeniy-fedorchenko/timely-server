package com.efedorchenko.timely.data.entity;

public enum EventStatus {

    SUCCESS, MISSED;

    public static EventStatus getDefault() {
        return SUCCESS;
    }
}
