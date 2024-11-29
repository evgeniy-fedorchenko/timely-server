package com.efedorchenko.timely.mapper;

import com.efedorchenko.timely.entity.UserDataEntity;
import com.efedorchenko.timely.entity.UserEntity;
import com.efedorchenko.timely.model.data.UserDataDto;
import com.efedorchenko.timely.model.data.UserDataType;

public abstract class AbstractMapper<E extends UserDataEntity, D extends UserDataDto> {

    abstract E map(D userDataDto, UserEntity userEntity);

    abstract D map(E userDataEntity);

    abstract E update(E oldDataEntity, D newDataDto);

    abstract UserDataType getUserDataType();

}
