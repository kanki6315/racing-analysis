-- V7 Migration: Switch to CSV Importer and Add Results Table

-- 1. Remove weather and report message fields from sessions
ALTER TABLE sessions
DROP
COLUMN IF EXISTS weather_air_temp,
  DROP
COLUMN IF EXISTS weather_track_temp,
  DROP
COLUMN IF EXISTS weather_condition,
  DROP
COLUMN IF EXISTS report_message;

-- 2. Remove lap/sector invalidation and best flags
ALTER TABLE laps
DROP
COLUMN IF EXISTS is_valid,
  DROP
COLUMN IF EXISTS is_personal_best,
  DROP
COLUMN IF EXISTS is_session_best,
  DROP
COLUMN IF EXISTS invalidation_reason;

ALTER TABLE sectors
DROP
COLUMN IF EXISTS is_valid,
  DROP
COLUMN IF EXISTS invalidation_reason;

-- 3. Add results table for parsed results from CSVs, referencing car_entry and using a join table for drivers
CREATE TABLE IF NOT EXISTS results
(
    id
    BIGSERIAL
    PRIMARY
    KEY,
    session_id
    BIGINT
    NOT
    NULL
    REFERENCES
    sessions
(
    id
),
    car_entry_id BIGINT NOT NULL REFERENCES car_entries
(
    id
),
    car_number VARCHAR
(
    10
) NOT NULL,
    tires VARCHAR
(
    50
),
    status VARCHAR
(
    50
),
    laps INTEGER,
    total_time VARCHAR
(
    50
),
    gap_first VARCHAR
(
    50
),
    gap_previous VARCHAR
(
    50
),
    fl_lapnum INTEGER,
    fl_time VARCHAR
(
    50
),
    fl_kph NUMERIC
(
    6,
    2
),
    position INTEGER
    );

-- 3b. Create join table for results and drivers (many-to-many)
CREATE TABLE IF NOT EXISTS results_drivers
(
    result_id
    BIGINT
    NOT
    NULL
    REFERENCES
    results
(
    id
) ON DELETE CASCADE,
    driver_id BIGINT NOT NULL REFERENCES drivers
(
    id
)
  ON DELETE CASCADE,
    driver_order INTEGER,
    PRIMARY KEY
(
    result_id,
    driver_id
)
    );

-- 4. Add index for fast lookup by session
CREATE INDEX IF NOT EXISTS idx_results_session_id ON results(session_id); 