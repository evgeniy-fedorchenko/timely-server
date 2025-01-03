package com.efedorchenko.timely.repository;

import com.efedorchenko.timely.entity.EntityType;
import com.efedorchenko.timely.entity.Event;
import com.efedorchenko.timely.entity.UserDataEntity;
import com.efedorchenko.timely.model.data.UserDataType;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

@EntityType(UserDataType.EVENT)
public interface EventRepository extends UserDataRepository<Event> {

    @Override
    @Query("SELECT e FROM Event e WHERE e.user.id = :userId AND e.monthUid BETWEEN :monthUidStart AND :monthUidEnd")
    List<Event> getListOfUserData(UUID userId, int monthUidStart, int monthUidEnd);

    @Override
    @Query("""
            SELECT e FROM Event e
            WHERE e.user.id = :#{#userDataEntity.user.id}
            AND e.monthUid = :#{#userDataEntity.monthUid}
            AND e.date = :#{#userDataEntity.date}
            """)
    List<Event> findData(UserDataEntity userDataEntity);
}
