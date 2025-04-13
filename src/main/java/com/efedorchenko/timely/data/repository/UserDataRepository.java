package com.efedorchenko.timely.data.repository;

import com.efedorchenko.timely.data.entity.EntityType;
import com.efedorchenko.timely.data.entity.UserDataEntity;
import com.efedorchenko.timely.data.model.UserDataType;
import com.efedorchenko.timely.exception.ExceptionTemplates;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Общий репозиторий для сущностей, наследующихся от {@link UserDataEntity}.
 * Используется для отделения наследующихся репозиториев от всех остальных JPA-репозиториев
 * для корректной работы фабрики репозиториев {@link UserDataRepositoryFactory}
 *
 * @param <E> параметр конкретной сущности, наследующейся от {@link UserDataEntity}
 */
@EntityType(UserDataType.PARENT)
public interface UserDataRepository<E extends UserDataEntity> extends JpaRepository<E, Long> {

    default List<E> findByUserIdAndMonthUidBetween(UUID userId, int monthUid, int monthUid2) {
        throw ExceptionTemplates.SVR_VAR_15.get();
    }

    default Optional<E> findByUserIdAndDate(UUID userId, LocalDate date) {
        throw ExceptionTemplates.SVR_VAR_15.get();
    }

    default List<E> getDataByUserIdAndChangedAtAfter(UUID userId, Instant changeAt) {
        throw ExceptionTemplates.SVR_VAR_15.get();
    }
}
