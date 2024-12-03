package com.efedorchenko.timely.repository;

import com.efedorchenko.timely.entity.EntityType;
import com.efedorchenko.timely.entity.UserDataEntity;
import com.efedorchenko.timely.exception.ErrorCode;
import com.efedorchenko.timely.exception.ServerException;
import com.efedorchenko.timely.model.data.UserDataType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
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
        throw new ServerException(ErrorCode.CONFIGURATION,
                """
                This query-method must be implemented in a specific subclass (interface).
                The basic implementation has no connection to the real table and is only needed to
                separate repositories working with UserDataEntity heirs from all other JPA repositories.
                Please implement this method in each subclass
                """
        );
    }
}
