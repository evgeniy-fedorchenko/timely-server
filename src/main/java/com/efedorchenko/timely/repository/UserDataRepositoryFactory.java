package com.efedorchenko.timely.repository;

import com.efedorchenko.timely.entity.EntityType;
import com.efedorchenko.timely.entity.UserDataEntity;
import com.efedorchenko.timely.model.data.UserDataType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class UserDataRepositoryFactory {

    private final Map<UserDataType, UserDataRepository<? extends UserDataEntity>> repositoryMap;

    public UserDataRepositoryFactory(List<UserDataRepository<? extends UserDataEntity>> allRepositories) {
        repositoryMap = new EnumMap<>(UserDataType.class);

        for (UserDataRepository<?> repository : allRepositories) {
            for (Class<?> interfaceClass : repository.getClass().getInterfaces()) {
                if (interfaceClass.isAnnotationPresent(EntityType.class)) {
                    UserDataType userDataType = interfaceClass.getAnnotation(EntityType.class).value();
                    repositoryMap.put(userDataType, repository);
                    break;
                }
                throw new IllegalStateException(("Some repository extending '%s<? extends %s>' does not have an " +
                        "associated entity class. Ensure the repository class is annotated with %s")
                        .formatted(
                                UserDataRepository.class.getName(),
                                UserDataEntity.class.getName(),
                                EntityType.class.getName())
                );
            }
        }
    }

    @SuppressWarnings("unchecked")
    public <E extends UserDataEntity> UserDataRepository<E> getRepository(UserDataType userDataType) {
        try {
            return ((UserDataRepository<E>) repositoryMap.get(userDataType));
        } catch (ClassCastException cce) {
            log.error("Could not get concrete UserDataRepository in factory from type '{}'. Ex: {}",
                    userDataType, cce.getMessage());
            throw cce;
        }
    }
}
