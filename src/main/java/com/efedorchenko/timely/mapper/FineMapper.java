package com.efedorchenko.timely.mapper;

import com.efedorchenko.timely.entity.Fine;
import com.efedorchenko.timely.entity.UserEntity;
import com.efedorchenko.timely.model.data.FineDto;
import com.efedorchenko.timely.model.data.UserDataType;
import com.efedorchenko.timely.service.Helper;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Package-private mapper
 * <p>
 * Для маппинга используйте методы {@link UserDataMapper}
 * с автоматическим определением конкретных {@code DTO} и {@code Entity}
 */
@Component
class FineMapper extends AbstractMapper<Fine, FineDto> {

    @Override
    Fine map(FineDto userDataDto, UserEntity userEntity) {
        Fine fine = new Fine();

        Optional.ofNullable(userDataDto.getBackendId()).ifPresent(fine::setId);
        fine.setDate(userDataDto.getDate());
        fine.setMonthUid(Helper.getMonthUid(userDataDto.getDate()));
        fine.setDescription(userDataDto.getDescription());
        fine.setAmount(userDataDto.getAmount());
        fine.setUser(userEntity);

        return fine;
    }

    @Override
    FineDto map(Fine userDataEntity) {
        return FineDto.builder()
                .backendId(userDataEntity.getId())
                .date(userDataEntity.getDate())
                .description(userDataEntity.getDescription())
                .amount(userDataEntity.getAmount())
                .build();
    }

    @Override
    Fine update(Fine oldDataEntity, FineDto newDataDto) {
        oldDataEntity.setDescription(newDataDto.getDescription());
        oldDataEntity.setAmount(newDataDto.getAmount());
        return oldDataEntity;
    }

    @Override
    UserDataType getUserDataType() {
        return UserDataType.FINE;
    }

}
