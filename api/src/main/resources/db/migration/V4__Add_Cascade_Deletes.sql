-- V4 Migration: Add Cascade Deletes from Event Downwards
-- This migration ensures that deleting an event will also delete all its associated data,
-- including sessions, car entries, laps, and sectors.

-- Step 1: Add ON DELETE CASCADE to sessions for event_id
-- This will delete all sessions associated with a deleted event.
ALTER TABLE sessions
DROP
CONSTRAINT IF EXISTS sessions_event_id_fkey,
ADD CONSTRAINT sessions_event_id_fkey
    FOREIGN KEY (event_id) REFERENCES events (id) ON DELETE
CASCADE;

-- Step 2: Add ON DELETE CASCADE to car_entries for session_id
-- This will delete all car entries associated with a deleted session.
ALTER TABLE car_entries
DROP
CONSTRAINT IF EXISTS car_entries_session_id_fkey,
ADD CONSTRAINT car_entries_session_id_fkey
    FOREIGN KEY (session_id) REFERENCES sessions (id) ON DELETE
CASCADE;

-- Step 3: Add ON DELETE CASCADE to laps for car_id (referencing car_entries)
-- This will delete all laps associated with a deleted car entry.
ALTER TABLE laps
DROP
CONSTRAINT IF EXISTS laps_car_id_fkey,
ADD CONSTRAINT laps_car_id_fkey
    FOREIGN KEY (car_id) REFERENCES car_entries (id) ON DELETE
CASCADE;

-- Step 4: Add ON DELETE CASCADE to car_drivers for car_id (referencing car_entries)
-- This will delete all driver records associated with a deleted car entry.
ALTER TABLE car_drivers
DROP
CONSTRAINT IF EXISTS car_drivers_car_id_fkey,
ADD CONSTRAINT car_drivers_car_id_fkey
    FOREIGN KEY (car_id) REFERENCES car_entries (id) ON DELETE
CASCADE;

-- Step 5: Add ON DELETE CASCADE to sectors for lap_id
-- This will delete all sectors associated with a deleted lap.
ALTER TABLE sectors
DROP
CONSTRAINT IF EXISTS sectors_lap_id_fkey,
ADD CONSTRAINT sectors_lap_id_fkey
    FOREIGN KEY (lap_id) REFERENCES laps (id) ON DELETE
CASCADE;

-- Step 6: Remove redundant foreign key constraint on car_drivers
-- that was created implicitly in V3 and was blocking cascade deletes.
-- The correct constraint with cascade delete was added in V4.

ALTER TABLE car_drivers
DROP
CONSTRAINT IF EXISTS car_drivers_car_entry_id_fkey;