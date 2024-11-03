CREATE TABLE users
(
    id       UUID PRIMARY KEY,
    name     varchar(128) NOT NULL,
    position varchar(32)  NOT NULL,
    rate     smallint     NOT NULL
);

CREATE TABLE user_details
(
    id          UUID PRIMARY KEY,
    username    varchar(64) UNIQUE NOT NULL,
    password    varchar(64)        NOT NULL,
    authorities varchar(32),
    FOREIGN KEY (id) REFERENCES users (id)
);

CREATE TABLE monthly_data_batches
(
    id        SERIAL PRIMARY KEY,
    month_uid int4 NOT NULL,
    events    jsonb,
    fines     jsonb,
    user_id   UUID NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (id)
);
CREATE UNIQUE INDEX unique_idx_month_uid_user_id ON monthly_data_batches (month_uid, user_id);



-- add UserData extension (Event)
INSERT INTO monthly_data_batches (month_uid, events, fines, user_id)
VALUES (:monthUID, jsonb_build_array(:newEvent::jsonb), '[]'::jsonb, :userId)
ON CONFLICT (month_uid, user_id) DO UPDATE
    SET events = monthly_data_batches.events || EXCLUDED.events
RETURNING 1;

-- remove UserData extension (Event)
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

-- delete row if events and fines are empty (must invoke after remove any UserData extension)
DELETE
FROM monthly_data_batches
WHERE user_id = :userId
  AND month_uid = :monthUID
  AND jsonb_array_length(events) = 0
  AND jsonb_array_length(fines) = 0;

-- select UserData extensions (Event) as userId and monthUID range
SELECT events
FROM monthly_data_batches
WHERE user_id = :userId
  AND month_uid BETWEEN :startMonthUid AND :endMonthUid;
