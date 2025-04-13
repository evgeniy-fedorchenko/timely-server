package com.efedorchenko.timely.data.mapper;

import com.efedorchenko.timely.data.entity.UserDataEntity;
import com.efedorchenko.timely.data.model.UserDataDto;
import com.efedorchenko.timely.data.model.UserDataType;
import com.efedorchenko.timely.exception.ExceptionTemplates;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class MapperFactory {

    private final Map<UserDataType, AbstractMapper<? extends UserDataEntity, ? extends UserDataDto>> mapperMap;

    public MapperFactory(List<AbstractMapper<? extends UserDataEntity, ? extends UserDataDto>> mapperList) {
        mapperMap = new EnumMap<>(UserDataType.class);
        mapperList.forEach(mapper -> mapperMap.put(mapper.getUserDataType(), mapper));
    }

    @SuppressWarnings("unchecked")
    public <E extends UserDataEntity, D extends UserDataDto> AbstractMapper<E, D> getMapper(UserDataType userDataType) {
        try {
            return (AbstractMapper<E, D>) mapperMap.get(userDataType);
        } catch (ClassCastException cce) {
            throw ExceptionTemplates.SVR_VAR12.apply(userDataType, mapperMap, cce);
        }
    }
}
