package com.efedorchenko.timely.repository;

import com.efedorchenko.timely.entity.EntityType;
import com.efedorchenko.timely.entity.UserDataEntity;
import com.efedorchenko.timely.exception.ExceptionTemplates;
import com.efedorchenko.timely.model.data.UserDataType;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

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
                throw ExceptionTemplates.SVR_VAR10.get();
            }
        }
    }

    @SuppressWarnings("unchecked")
    public <E extends UserDataEntity> UserDataRepository<E> getRepository(UserDataType userDataType) {
        try {
            return ((UserDataRepository<E>) repositoryMap.get(userDataType));
        } catch (ClassCastException cce) {
            throw ExceptionTemplates.SVR_VAR11.apply(userDataType, repositoryMap, cce);
        }
    }
}
