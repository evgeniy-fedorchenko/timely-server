package com.efedorchenko.timely.mapper;

import com.efedorchenko.timely.entity.Space;
import com.efedorchenko.timely.entity.UserEntity;
import com.efedorchenko.timely.model.SpaceCreateDto;
import com.efedorchenko.timely.model.SpaceKeys;
import org.springframework.stereotype.Component;

@Component
public class SpaceMapper {

    public Space map(SpaceCreateDto request, SpaceKeys keys, UserEntity creator) {
        Space space = new Space();

        space.setName(request.getName());
        space.setWorkerKey(keys.getWorkerKey());
        space.setBossKey(keys.getBossKey());
        space.setCreator(creator);

        return space;
    }
}
