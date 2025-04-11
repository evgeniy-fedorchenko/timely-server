package com.efedorchenko.timely.entity;

public enum EventStatus {

    SUCCESS, MISSED;

    public static EventStatus getDefault() {
        return SUCCESS;
    }
}
