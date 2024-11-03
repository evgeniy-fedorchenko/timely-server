package com.efedorchenko.timely.repository;

import com.efedorchenko.timely.entity.MonthlyDataBatch;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface MonthlyDataBatchRepository extends R2dbcRepository<MonthlyDataBatch, Long> {

    @Modifying
    @Transactional
    @Query("""
            INSERT INTO monthly_data_batches (month_uid, events, fines, user_id)
            VALUES (:monthUID, jsonb_build_array(:newEvent::jsonb), '[]'::jsonb, :userId)
            ON CONFLICT (month_uid, user_id) DO UPDATE
                SET events = monthly_data_batches.events || EXCLUDED.events
            RETURNING 1;
            """)
    Mono<Integer> addEvent(UUID userId, int monthUID, String newEvent);

    @Modifying
    @Transactional
    @Query("""
            INSERT INTO monthly_data_batches (month_uid, events, fines, user_id)
            VALUES (:monthUID, '[]'::jsonb, jsonb_build_array(:newFine::jsonb), :userId)
            ON CONFLICT (month_uid, user_id) DO UPDATE
                SET fines = monthly_data_batches.fines || EXCLUDED.fines
            RETURNING 1;
            """)
    Mono<Integer> addFine(UUID userId, int monthUID, String newFine);

    @Modifying
    @Query("""
            UPDATE monthly_data_batches
            SET events = COALESCE(
                    (SELECT jsonb_agg(elem)
                     FROM jsonb_array_elements(events) AS elem
                     WHERE elem <> :eventToRemove::jsonb),
                    '[]'::jsonb
                         )
            WHERE user_id = :userId
              AND month_uid = :monthUID
            RETURNING monthly_data_batches.id;
            """)
    Mono<Long> removeEvent(UUID userId, int monthUID, String eventToRemove);

    @Modifying
    @Query("""
            UPDATE monthly_data_batches
            SET fines = COALESCE(
                    (SELECT jsonb_agg(elem)
                     FROM jsonb_array_elements(fines) AS elem
                     WHERE elem <> :fineToRemove::jsonb), '[]'::jsonb
                         )
            WHERE user_id = :userId
              AND month_uid = :monthUID
            RETURNING id;
            """)
    Mono<Long> removeFine(UUID userId, int monthUID, String fineToRemove);

    @Modifying
    @Query("""
            DELETE FROM monthly_data_batches
            WHERE user_id = :userId
              AND month_uid = :monthUID
              AND jsonb_array_length(events) = 0
              AND jsonb_array_length(fines) = 0;
            """)
    Mono<Void> deleteIfEmpty(UUID userId, int monthUID);

    @Transactional(readOnly = true)
    @Query("""
            SELECT events
            FROM monthly_data_batches
            WHERE user_id = :userId
              AND month_uid BETWEEN :startMonthUid AND :endMonthUid;
            """)
    Flux<String> findEventsFromRange(UUID userId, int startMonthUid, int endMonthUid);

    @Transactional(readOnly = true)
    @Query("""
            SELECT fines
            FROM monthly_data_batches
            WHERE user_id = :userId
              AND month_uid BETWEEN :startMonthUid AND :endMonthUid
            """)
    Flux<String> findFinesFromRange(UUID userId, int startMonthUid, int endMonthUid);

    @Transactional(readOnly = true)
    Flux<MonthlyDataBatch> findAllByUserIdAndMonthUIDBetween(UUID userId, int startMonthUid, int endMonthUid);

}
