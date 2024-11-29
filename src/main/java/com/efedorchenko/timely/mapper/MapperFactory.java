package com.efedorchenko.timely.mapper;

import com.efedorchenko.timely.entity.UserDataEntity;
import com.efedorchenko.timely.model.data.UserDataDto;
import com.efedorchenko.timely.model.data.UserDataType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Slf4j
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
            log.error("Could not get concrete UserDataMapper in factory from type '{}'. Ex: {}",
                    userDataType, cce.getMessage());
            throw cce;
        }
    }
}
