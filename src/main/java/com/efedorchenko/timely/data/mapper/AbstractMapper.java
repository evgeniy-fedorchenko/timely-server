package com.efedorchenko.timely.data.mapper;

import com.efedorchenko.timely.data.entity.UserDataEntity;
import com.efedorchenko.timely.data.entity.UserEntity;
import com.efedorchenko.timely.data.model.UserDataDto;
import com.efedorchenko.timely.data.model.UserDataType;

public abstract class AbstractMapper<E extends UserDataEntity, D extends UserDataDto> {

    abstract E map(D userDataDto, UserEntity userEntity);

    abstract D map(E userDataEntity);

    abstract E update(E oldDataEntity, D newDataDto);

    abstract UserDataType getUserDataType();

}
