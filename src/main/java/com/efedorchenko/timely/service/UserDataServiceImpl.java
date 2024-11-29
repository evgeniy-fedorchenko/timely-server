package com.efedorchenko.timely.service;

import com.efedorchenko.timely.entity.UserDataEntity;
import com.efedorchenko.timely.entity.UserEntity;
import com.efedorchenko.timely.logging.Log;
import com.efedorchenko.timely.mapper.UserDataMapper;
import com.efedorchenko.timely.model.data.DataRangeRequest;
import com.efedorchenko.timely.model.data.UserDataDto;
import com.efedorchenko.timely.model.data.UserDataModifyDto;
import com.efedorchenko.timely.model.data.UserDataType;
import com.efedorchenko.timely.repository.UserDataRepository;
import com.efedorchenko.timely.repository.UserDataRepositoryFactory;
import com.efedorchenko.timely.repository.UserEntityRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Slf4j
@Log
@Service
@RequiredArgsConstructor
public class UserDataServiceImpl implements UserDataService<UserDataDto, DataRangeRequest> {

    private final ExecutorService executorOfVirtual;
    private final UserDataMapper userDataMapper;
    private final UserEntityRepository userEntityRepository;
    private final UserDataRepositoryFactory repositoryFactory;

    @Override
    @Transactional
    public void addData(UUID userId, UserDataDto userDataDto) {
        CompletableFuture.runAsync(() -> {
            UserEntity userEntity = userEntityRepository.findById(userId).orElseThrow();
            UserDataEntity userDataEntity = userDataMapper.map(userDataDto, userEntity);

            UserDataRepository<UserDataEntity> repository = repositoryFactory.getRepository(userDataDto.getType());
            repository.save(userDataEntity);

        }, executorOfVirtual);
    }

    @Override
    @Transactional
    public void deleteData(UUID userId, UserDataType userDataType, Long dataId) {
        CompletableFuture.runAsync(() -> {
            UserDataRepository<UserDataEntity> repository = repositoryFactory.getRepository(userDataType);
            repository.findById(dataId).ifPresentOrElse(data -> {
                if (data.getUser().getId() != userId) {
                    log.warn("UserDataObject [{}] does not match UserEntity with id [{}] for deleting", dataId, userId);
                    return;
                }
                repository.deleteById(dataId);

            }, () -> log.warn("UserDataObject [{}] not found for deleting", userId));

        }, executorOfVirtual);
    }

    @Override
    @Transactional(readOnly = true)
    public CompletableFuture<Collection<UserDataDto>> getRange(
            UUID userId, DataRangeRequest dataRangeRequest, UserDataType dataType) {

        return CompletableFuture.supplyAsync(() -> {
            int startMonthUid = Helper.getMonthUid(dataRangeRequest.getStart());
            int endMonthUid = Helper.getMonthUid(dataRangeRequest.getEnd());
            UserDataRepository<UserDataEntity> repository = repositoryFactory.getRepository(dataType);
            List<UserDataEntity> foundEntities = repository.getListOfUserData(userId, startMonthUid, endMonthUid);

            if (foundEntities.isEmpty()) {
                return Collections.emptyList();
            }

            ArrayList<UserDataDto> dtos = new ArrayList<>();
            for (UserDataEntity entity : foundEntities) {
                dtos.add(userDataMapper.map(entity));
            }
            return dtos;

        }, executorOfVirtual);
    }

    @Override
    @Transactional
    public void changeData(UUID userId, UserDataModifyDto modifyingData) {
        UserDataDto newData = modifyingData.getNewData();
        Long dataId = newData.getId();
        if (dataId == null) {
            String errMess = "Cannot modify userData because data id is null. Provided data: [%s]"
                    .formatted(modifyingData.toString());
            throw new IllegalArgumentException(errMess);
        }
        UserDataRepository<UserDataEntity> repository = repositoryFactory.getRepository(newData.getType());
        UserDataEntity dataEntity = repository.findById(dataId).orElseThrow(() ->
                new EntityNotFoundException("User data [%s] for modifying is not found".formatted(modifyingData))
        );

        if (!modifyingData.getModifyingUserId().equals(dataEntity.getUser().getId())) {
            String errMess = "User data found [%s], but owner id does not equal with modifyingUserId [%s]"
                    .formatted(dataEntity, modifyingData.getModifyingUserId());
            throw new IllegalArgumentException(errMess);
        }

        CompletableFuture.runAsync(() -> {
            UserDataEntity updatedDataEntity = userDataMapper.update(dataEntity, newData);
            repository.save(updatedDataEntity);
        }, executorOfVirtual);
    }
}
