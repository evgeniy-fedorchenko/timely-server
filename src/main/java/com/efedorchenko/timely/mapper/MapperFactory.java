package com.efedorchenko.timely.mapper;

import com.efedorchenko.timely.entity.UserDataEntity;
import com.efedorchenko.timely.exception.ServerException;
import com.efedorchenko.timely.model.data.UserDataDto;
import com.efedorchenko.timely.model.data.UserDataType;
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
            return ((AbstractMapper<E, D>) mapperMap.get(userDataType));
        } catch (ClassCastException cce) {
            String errMess = "Could not get concrete UserDataMapper in factory from type [%s], available only [%s]. Ex: %s"
                    .formatted(userDataType, mapperMap.toString(), cce.getMessage());
            throw new ServerException(errMess, cce);
        }
    }
}
