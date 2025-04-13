package com.efedorchenko.timely.data.mapper;

import com.efedorchenko.timely.data.entity.Space;
import com.efedorchenko.timely.data.entity.UserEntity;
import com.efedorchenko.timely.data.model.SpaceDto;
import com.efedorchenko.timely.data.model.SpaceKeys;
import org.springframework.stereotype.Component;

@Component
public class SpaceMapper {

    public Space map(SpaceDto request, SpaceKeys keys, UserEntity creator) {
        Space space = new Space();

        space.setName(request.getName());
        space.setWorkerKey(keys.getWorkerKey());
        space.setBossKey(keys.getBossKey());
        space.setCreator(creator);

        return space;
    }

    public SpaceDto map(Space space) {
        return new SpaceDto(space.getName());
    }
}
