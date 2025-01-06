package com.efedorchenko.timely.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

@Getter
@ToString
@AllArgsConstructor
public class SpaceMember {

    private final UUID userId;
    private final String name;
    private final String position;
}
