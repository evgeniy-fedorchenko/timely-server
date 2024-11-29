package com.efedorchenko.timely.mapper;

import com.efedorchenko.timely.entity.Event;
import com.efedorchenko.timely.entity.UserEntity;
import com.efedorchenko.timely.model.data.EventDto;
import com.efedorchenko.timely.model.data.UserDataType;
import com.efedorchenko.timely.service.Helper;
import org.springframework.stereotype.Component;

/**
 * Package-private mapper
 * <p>
 * Для маппинга используйте методы {@link UserDataMapper}
 * с автоматическим определением конкретных {@code DTO} и {@code Entity}
 */
@Component
class EventMapper extends AbstractMapper<Event, EventDto> {

    @Override
    Event map(EventDto userDataDto, UserEntity userEntity) {
        Event event = new Event();

        event.setDate(userDataDto.getDate());
        event.setMonthUid(Helper.getMonthUid(userDataDto.getDate()));
        event.setWorkDuration(userDataDto.getWorkDuration());
        event.setComment(userDataDto.getComment());
        event.setUser(userEntity);

        return event;
    }

    @Override
    EventDto map(Event userDataEntity) {
        return EventDto.builder()
                .id(userDataEntity.getId())
                .date(userDataEntity.getDate())
                .workDuration(userDataEntity.getWorkDuration())
                .comment(userDataEntity.getComment())
                .build();
    }

    @Override
    Event update(Event oldDataEntity, EventDto newDataDto) {
        oldDataEntity.setWorkDuration(newDataDto.getWorkDuration());
        oldDataEntity.setComment(newDataDto.getComment());
        return oldDataEntity;
    }

    @Override
    UserDataType getUserDataType() {
        return UserDataType.EVENT;
    }
}
