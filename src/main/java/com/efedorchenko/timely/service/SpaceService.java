package com.efedorchenko.timely.service;

import com.efedorchenko.timely.entity.Space;
import com.efedorchenko.timely.model.SpaceCreateDto;
import com.efedorchenko.timely.model.SpaceKeys;
import com.efedorchenko.timely.security.model.RoleType;
import org.springframework.lang.Nullable;

import java.util.UUID;

public interface SpaceService {

    SpaceKeys create(UUID userId, SpaceCreateDto spaceCreateDto);

    SpaceKeys create(UUID userId, SpaceCreateDto spaceCreateDto, SpaceKeys spaceKeys);

    @Nullable
    Space findSpace(String spaceKey, RoleType roleType);

    @Nullable
    SpaceKeys getKeys(UUID userId);

    SpaceKeys createDetachedKeys();
}
