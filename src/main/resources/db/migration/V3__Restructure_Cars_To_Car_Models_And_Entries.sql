-- V3 Migration: Restructure Cars to Car Models and Car Entries
-- This migration separates the concept of car models (e.g., "Porsche 911 GT3 R") 
-- from individual car entries in events (e.g., "Car #01 from Team BMW")

-- Step 1: Create the new car_models table
CREATE TABLE car_models
(
    id              BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    manufacturer_id BIGINT REFERENCES manufacturers (id),
    name            VARCHAR(255) NOT NULL, -- e.g., "911 GT3 R", "M4 GT3 EVO"
    full_name       VARCHAR(500),          -- e.g., "Porsche 911 GT3 R"
    year_model      INTEGER,               -- e.g., 2023, 2024
    description     TEXT,
    UNIQUE (manufacturer_id, name, year_model)
);

-- Step 2: Create the new car_entries table
CREATE TABLE car_entries
(
    id            BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    session_id    BIGINT REFERENCES sessions (id),
    team_id       BIGINT REFERENCES teams (id),
    class_id      BIGINT REFERENCES classes (id),
    car_model_id  BIGINT REFERENCES car_models (id),
    number        VARCHAR(10) NOT NULL, -- Car number in the event
    tire_supplier VARCHAR(50),
    UNIQUE (session_id, number)         -- Ensure unique car numbers per session
);

-- Step 3: Migrate existing car data to the new structure
-- First, create car models from existing car data
INSERT INTO car_models (manufacturer_id, name, full_name, year_model, description)
SELECT DISTINCT c.manufacturer_id,
                c.model,
                m.name || ' ' || c.model            as full_name,
                EXTRACT(YEAR FROM s.start_datetime) as year_model,
                'Migrated from existing car data'   as description
FROM cars c
         JOIN sessions s ON c.session_id = s.id
         JOIN manufacturers m ON c.manufacturer_id = m.id
WHERE c.model IS NOT NULL
  AND c.model != '';

-- Step 4: Create car entries from existing car data
INSERT INTO car_entries (session_id, team_id, class_id, car_model_id, number, tire_supplier)
SELECT c.session_id,
       c.team_id,
       c.class_id,
       cm.id as car_model_id,
       c.number,
       c.tire_supplier
FROM cars c
         JOIN car_models cm ON cm.manufacturer_id = c.manufacturer_id
    AND cm.name = c.model
    AND cm.year_model = EXTRACT(YEAR FROM (SELECT s.start_datetime
                                           FROM sessions s
                                           WHERE s.id = c.session_id));

-- Step 5: Create temporary mapping table to track old car IDs to new car entry IDs
CREATE
TEMPORARY TABLE car_id_mapping (
    old_car_id BIGINT,
    new_car_entry_id BIGINT
);

INSERT INTO car_id_mapping (old_car_id, new_car_entry_id)
SELECT c.id  as old_car_id,
       ce.id as new_car_entry_id
FROM cars c
         JOIN car_entries ce ON ce.session_id = c.session_id
    AND ce.team_id = c.team_id
    AND ce.class_id = c.class_id
    AND ce.number = c.number;

-- Step 6: Update car_drivers table to reference car_entries instead of cars
ALTER TABLE car_drivers
    ADD COLUMN car_entry_id BIGINT REFERENCES car_entries (id);

UPDATE car_drivers cd
SET car_entry_id = cim.new_car_entry_id FROM car_id_mapping cim
WHERE cd.car_id = cim.old_car_id;

-- Step 7: Update laps table to reference car_entries instead of cars
ALTER TABLE laps
    ADD COLUMN car_entry_id BIGINT REFERENCES car_entries (id);

UPDATE laps l
SET car_entry_id = cim.new_car_entry_id FROM car_id_mapping cim
WHERE l.car_id = cim.old_car_id;

-- Step 8: Drop the old foreign key constraints
ALTER TABLE car_drivers DROP CONSTRAINT IF EXISTS car_drivers_car_id_fkey;
ALTER TABLE laps DROP CONSTRAINT IF EXISTS laps_car_id_fkey;

-- Step 9: Drop the old car_id columns and add new constraints
ALTER TABLE car_drivers DROP COLUMN car_id;
ALTER TABLE laps DROP COLUMN car_id;

-- Step 10: Rename the new columns to match the expected names
ALTER TABLE car_drivers RENAME COLUMN car_entry_id TO car_id;
ALTER TABLE laps RENAME COLUMN car_entry_id TO car_id;

-- Step 11: Add the foreign key constraints for the new structure
ALTER TABLE car_drivers
    ADD CONSTRAINT car_drivers_car_id_fkey
        FOREIGN KEY (car_id) REFERENCES car_entries (id);

ALTER TABLE laps
    ADD CONSTRAINT laps_car_id_fkey
        FOREIGN KEY (car_id) REFERENCES car_entries (id);

-- Step 12: Drop the old cars table
DROP TABLE cars;

-- Step 13: Clean up temporary table
DROP TABLE car_id_mapping;