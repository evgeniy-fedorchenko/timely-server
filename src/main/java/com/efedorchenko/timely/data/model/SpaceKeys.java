package com.efedorchenko.timely.data.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class SpaceKeys {

    private final String workerKey;

    private final String bossKey;

}
