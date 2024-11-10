package com.efedorchenko.timely.service;

import com.efedorchenko.timely.entity.MonthlyDataBatch;
import com.efedorchenko.timely.entity.UserData;
import com.efedorchenko.timely.model.DataRangeRequest;
import com.efedorchenko.timely.model.EventsAndFines;
import com.efedorchenko.timely.model.UserDataType;
import com.efedorchenko.timely.repository.MonthlyDataBatchRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

@Slf4j
@Service
@AllArgsConstructor
public class UserDataServiceImpl<T extends UserData> implements UserDataService<T> {


    private final ObjectMapper objectMapper;
    private final MonthlyDataBatchRepository dataRepository;

   /* @Log
    @Override
    public Mono<Void> addData(UUID userId, T userData) {
        return Mono.fromRunnable(() -> {
            LocalDate date = userData.getDate();
            int monthUID = getMonthUID(YearMonth.of(date.getYear(), date.getMonth()));

            Mono.defer(() -> switch (userData.getType()) {
                                case EVENT -> dataRepository.addEvent(userId, monthUID, serialize(userData));
                                case FINE -> dataRepository.addFine(userId, monthUID, serialize(userData));
                            }

                    ).doOnSuccess(id -> log.trace("User's data added, id: {}, data: {}", id, userData))
                    .doOnError(error -> log.error("Failed to add user's data. Ex: ", error))
                    .subscribeOn(Schedulers.boundedElastic())
                    .subscribe();
        }).then();
    }

//    @Log
    @Override
    @Transactional
    public Mono<Void> removeData(UUID userId, T userData) {
        return Mono.fromRunnable(() -> {
            LocalDate date = userData.getDate();
            int monthUID = getMonthUID(YearMonth.of(date.getYear(), date.getMonth()));

            Mono.defer(() -> switch (userData.getType()) {
                                case EVENT -> dataRepository.removeEvent(userId, monthUID, serialize(userData));
                                case FINE -> dataRepository.removeFine(userId, monthUID, serialize(userData));
                            }

                    ).flatMap(id -> dataRepository.deleteIfEmpty(userId, monthUID).thenReturn(id))
                    .doOnSuccess(id -> log.trace("User's data removed, id: {}, data: {}", id, userData))
                    .doOnError(error -> log.error("Failed to remove user's data. Ex: ", error))
                    .subscribeOn(Schedulers.boundedElastic())
                    .subscribe();
        }).then();
    }

//    @Log
    @Override
    public Flux<T> getRange(UUID userId, DataRangeRequest dataRangeRequest, UserDataType dataType) {
        int startMonthUID = getMonthUID(dataRangeRequest.getStart());
        int endMonthUID = getMonthUID(dataRangeRequest.getEnd());

        return Flux.defer(() ->
                switch (dataType) {
                    case EVENT -> dataRepository.findEventsFromRange(userId, startMonthUID, endMonthUID);
                    case FINE -> dataRepository.findFinesFromRange(userId, startMonthUID, endMonthUID);
                }

        ).flatMap(eventsJson -> {
            try {
                CollectionType listType = objectMapper.getTypeFactory()
                        .constructCollectionType(List.class, UserData.class);

                List<T> dataObjects = objectMapper.readValue(eventsJson, listType);
                return Flux.fromIterable(dataObjects);

            } catch (JsonProcessingException jpe) {
                return Flux.error(new RuntimeException("Failed to parse events JSON", jpe));
            }
        });
    }

    @Log
    @Override
    public Mono<EventsAndFines> getRange(UUID userId, DataRangeRequest dataRangeRequest) {
        int startMonthUID = getMonthUID(dataRangeRequest.getStart());
        int endMonthUID = getMonthUID(dataRangeRequest.getEnd());

        return Mono.from(dataRepository.findAllByUserIdAndMonthUIDBetween(userId, startMonthUID, endMonthUID)
                .collectList()
                .map(batches -> new EventsAndFines(
                        deserializeList(batches, MonthlyDataBatch::getEvents),
                        deserializeList(batches, MonthlyDataBatch::getFines)
                ))
        );
    }*/

    private <D> List<D> deserializeList(List<MonthlyDataBatch> batches,
                                        Function<MonthlyDataBatch, String> jsonGetter) {
        return batches.stream()
                .flatMap(batch -> {
                    try {
                        return objectMapper
                                .readValue(jsonGetter.apply(batch), new TypeReference<List<D>>() {})
                                .stream();
                    } catch (JsonProcessingException jpe) {
                        throw new RuntimeException(
                                "Failed to deserialize batches. Batches: %s. Ex:".formatted(batch), jpe);
                    }
                })
                .toList();
    }

    private String serialize(T userData) {
        try {
            return objectMapper.writeValueAsString(userData);
        } catch (JsonProcessingException jpe) {
            throw new RuntimeException("Failed to serialize userData. UserData: %s. Ex:".formatted(userData), jpe);
        }
    }

    private int getMonthUID(YearMonth yearMonth) {
        return yearMonth.getYear() * 100 + yearMonth.getMonthValue();
    }

    @Override
    public Void addData(UUID userId, T event) {
        return null;
    }

    @Override
    public Void removeData(UUID userId, T userData) {
        return null;
    }

    @Override
    public T getRange(UUID userId, DataRangeRequest dataRangeRequest, UserDataType dataType) {
        return null;
    }

    @Override
    public EventsAndFines getRange(UUID userId, DataRangeRequest dataRangeRequest) {
        return null;
    }
}
