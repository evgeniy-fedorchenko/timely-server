package com.efedorchenko.timely.repository;

import com.efedorchenko.timely.entity.MonthlyDataBatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public interface MonthlyDataBatchRepository extends JpaRepository<MonthlyDataBatch, Long> {

    @Modifying
    @Transactional
    @Query(value = """
            INSERT INTO monthly_data_batches (month_uid, events, fines, user_id)
            VALUES (:monthUID, jsonb_build_array(:newEvent::jsonb), '[]'::jsonb, :userId)
            ON CONFLICT (month_uid, user_id) DO UPDATE
                SET events = monthly_data_batches.events || EXCLUDED.events
            RETURNING 1;
            """, nativeQuery = true)
    Integer addEvent(UUID userId, int monthUID, String newEvent);

    @Modifying
    @Transactional
    @Query(value = """
            INSERT INTO monthly_data_batches (month_uid, events, fines, user_id)
            VALUES (:monthUID, '[]'::jsonb, jsonb_build_array(:newFine::jsonb), :userId)
            ON CONFLICT (month_uid, user_id) DO UPDATE
                SET fines = monthly_data_batches.fines || EXCLUDED.fines
            RETURNING 1;
            """, nativeQuery = true)
    Integer addFine(UUID userId, int monthUID, String newFine);

    @Modifying
    @Query(value = """
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
            """, nativeQuery = true)
    Long removeEvent(UUID userId, int monthUID, String eventToRemove);

    @Modifying
    @Query(value = """
            UPDATE monthly_data_batches
            SET fines = COALESCE(
                    (SELECT jsonb_agg(elem)
                     FROM jsonb_array_elements(fines) AS elem
                     WHERE elem <> :fineToRemove::jsonb), '[]'::jsonb
                         )
            WHERE user_id = :userId
              AND month_uid = :monthUID
            RETURNING id;
            """, nativeQuery = true)
    Long removeFine(UUID userId, int monthUID, String fineToRemove);

    @Modifying
    @Query(value = """
            DELETE FROM monthly_data_batches
            WHERE user_id = :userId
              AND month_uid = :monthUID
              AND jsonb_array_length(events) = 0
              AND jsonb_array_length(fines) = 0;
            """, nativeQuery = true)
    Void deleteIfEmpty(UUID userId, int monthUID);

    @Transactional(readOnly = true)
    @Query(value = """
            SELECT events
            FROM monthly_data_batches
            WHERE user_id = :userId
              AND month_uid BETWEEN :startMonthUid AND :endMonthUid;
            """, nativeQuery = true)
    String findEventsFromRange(UUID userId, int startMonthUid, int endMonthUid);

    @Transactional(readOnly = true)
    @Query(value = """
            SELECT fines
            FROM monthly_data_batches
            WHERE user_id = :userId
              AND month_uid BETWEEN :startMonthUid AND :endMonthUid
            """, nativeQuery = true)
    String findFinesFromRange(UUID userId, int startMonthUid, int endMonthUid);

    @Transactional(readOnly = true)
    MonthlyDataBatch findAllByUserIdAndMonthUIDBetween(UUID userId, int startMonthUid, int endMonthUid);

}
