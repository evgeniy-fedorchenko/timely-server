package com.efedorchenko.timely.repository;

import com.efedorchenko.timely.entity.EntityType;
import com.efedorchenko.timely.entity.UserDataEntity;
import com.efedorchenko.timely.exception.ExceptionTemplates;
import com.efedorchenko.timely.model.data.UserDataType;
import org.springframework.data.jpa.repository.JpaRepository;

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

    default List<E> getListOfUserData(UUID userId, int monthUidStart, int monthUidEnd) {
        throw ExceptionTemplates.SVR_VAR_15.get();
    }

     default Optional<E> findByUserIdAndDate(UUID userId, LocalDate date) {
         throw ExceptionTemplates.SVR_VAR_15.get();
     }
}
