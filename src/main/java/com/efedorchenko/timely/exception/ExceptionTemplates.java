package com.efedorchenko.timely.exception;

import com.efedorchenko.timely.entity.EntityType;
import com.efedorchenko.timely.entity.UserDataEntity;
import com.efedorchenko.timely.model.data.UserDataModifyDto;
import com.efedorchenko.timely.model.data.UserDataType;
import com.efedorchenko.timely.repository.UserDataRepository;
import com.efedorchenko.timely.security.model.RoleType;
import org.apache.commons.lang3.function.TriFunction;

import java.util.Map;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class ExceptionTemplates {

    public static final Supplier<BusinessException> BNS_VAR1 =
            () -> {
                String errMess = "To add data to another user, you need to provide his userId in DTO (field 'toUserId')";
                return new BusinessException(ErrorCode.REQUIRES_NOT_NULL, errMess);
            };

    public static final Function<UUID, ServerException> SVR_VAR2 =
            initiatorIdOfAdding -> {
                String errMess = "Failed to find the role of the user who was authorized. User: [%s]. Please check how he passed the filters"
                        .formatted(initiatorIdOfAdding.toString());
                return new ServerException(ErrorCode.RESOURCE_NOT_FOUND, errMess);
            };

    public static final Function<UUID, ServerException> SVR_VAR3 =
            (initiatorIdOfAdding) -> {
                String errMess = "Failed to find the space to which the operation initiator belongs. InitiatorID: [%s]. Please check how this user was created"
                        .formatted(initiatorIdOfAdding.toString());
                return new ServerException(ErrorCode.RESOURCE_NOT_FOUND, errMess);
            };

    public static final BiFunction<UUID, UUID, BusinessException> BNS_VAR4 =
            (addableUserId, initiatorIdOfAdding) -> {
                String errMess = "Failed to find the user on whom the operation should be performed. UserID: [%s]. InitiatorID: [%s]. Please check how this user was created"
                        .formatted(addableUserId.toString(), initiatorIdOfAdding.toString());
                return new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, errMess);
            };

    public static final BiFunction<UUID, UUID, BusinessException> BNS_VAR5 =
            (initiatorIdOfAdding, toUserId) -> {
                String errMess = "Initiator (id: %s) is trying to execute operation for data to user (id: %s) not from his own space"
                        .formatted(initiatorIdOfAdding.toString(), toUserId.toString());
                return new BusinessException(ErrorCode.FORBIDDEN, errMess);
            };

    public static final BiFunction<Long, UUID, BusinessException> BNS_VAR6 =
            (dataId, userId) -> {
                String errMess = "Data of userID [%s] does not match UserEntity with id [%s] for deleting"
                        .formatted(dataId, userId);
                return new BusinessException(ErrorCode.INVALID_DATA, errMess);
            };

    public static final Function<UserDataModifyDto, BusinessException> BNS_VAR7 =
            modifyingData -> {
                String errMess = "Cannot modify userData because data id is null. Provided data: [%s]"
                        .formatted(modifyingData.toString());
                throw new BusinessException(ErrorCode.REQUIRES_NOT_NULL, errMess);
            };

    public static final Function<UserDataModifyDto, BusinessException> BNS_VAR8 =
            modifyingData -> {
                String errMess = "User data [%s] for modifying is not found".formatted(modifyingData);
                return new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, errMess);
            };

    public static final BiFunction<UserDataEntity, UUID, BusinessException> BNS_VAR9 =
            (dataEntity, modifyingUserId) -> {
                String errMess = "User data found [%s], but owner id does not equal with modifyingUserId [%s]"
                        .formatted(dataEntity, modifyingUserId);
                return new BusinessException(ErrorCode.INVALID_DATA, errMess);
            };

    public static final Supplier<ServerException> SVR_VAR10 =
            () -> {
                String errMess = "Some repository extending [%s<? extends %s>] does not have an associated entity class. Ensure the repository class is annotated with [%s]"
                        .formatted(UserDataRepository.class.getName(), UserDataEntity.class.getName(), EntityType.class.getName());
                return new ServerException(ErrorCode.CONFIGURATION, errMess);
            };

    public static final TriFunction<UserDataType, Map<?, ?>, Exception, ServerException> SVR_VAR11 =
            (userDataType, repositoryMap, exception) -> {
                String errMess = "Could not get concrete UserDataRepository in factory from type [%s], available only [%s]. Ex: %s"
                        .formatted(userDataType, repositoryMap.toString(), exception.getMessage());
                return new ServerException(ErrorCode.CONFIGURATION, errMess, exception);
            };

    public static final TriFunction<UserDataType, Map<?, ?>, Exception, ServerException> SVR_VAR12 =
            (userDataType, mapperMap, exception) -> {
                String errMess = "Could not get concrete UserDataMapper in factory from type [%s], available only [%s]. Ex: %s"
                        .formatted(userDataType, mapperMap.toString(), exception.getMessage());
                throw new ServerException(ErrorCode.CONFIGURATION, errMess, exception);
            };

    public static final BiFunction<UUID, RoleType, ServerException> SVR_VAR13 =
            (userId, role) -> {
                String errMess = "User not found with id [%s] for add role: [%s] , please check why he was authorized"
                        .formatted(userId.toString(), role);
                return new ServerException(ErrorCode.RESOURCE_NOT_FOUND, errMess);
            };

    public static final Function<RoleType, ServerException> SVR_VAR14 =
            role -> {
                String errMess = "Role [%s] not found, available roles only [%s]. Please check how validation allowed this type"
                        .formatted(role, RoleType.values());
                return new ServerException(ErrorCode.NOT_FOUND_FOR_SERVER_EX, errMess);
            };

    public static final Supplier<ServerException> SVR_VAR_15 =
            () -> {
                String errMess = """
                        This query-method must be implemented in a specific subclass (interface).
                        The basic implementation has no connection to the real table and is only needed to
                        separate repositories working with UserDataEntity heirs from all other JPA repositories.
                        Please implement this method in each subclass
                        """;
                return new ServerException(ErrorCode.CONFIGURATION, errMess);
            };
}
