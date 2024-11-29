package com.efedorchenko.timely.mapper;

import com.efedorchenko.timely.entity.UserDataEntity;
import com.efedorchenko.timely.entity.UserEntity;
import com.efedorchenko.timely.model.data.UserDataDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class UserDataMapper {

    private final MapperFactory mapperFactory;

    public <E extends UserDataEntity, D extends UserDataDto> E map(D userDataDto, UserEntity userEntity) {
        AbstractMapper<E, D> mapper = mapperFactory.getMapper(userDataDto.getType());
        return mapper.map(userDataDto, userEntity);
    }

    public <E extends UserDataEntity, D extends UserDataDto> D map(E userDataEntity) {
        AbstractMapper<E, D> mapper = mapperFactory.getMapper(userDataEntity.getType());
        return mapper.map(userDataEntity);
    }

    public <E extends UserDataEntity, D extends UserDataDto> E update(E oldDataEntity, D newDataDto) {
        AbstractMapper<E, D> mapper = mapperFactory.getMapper(newDataDto.getType());
        return mapper.update(oldDataEntity, newDataDto);
    }
}
