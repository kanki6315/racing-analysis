-- Make sector_time_seconds nullable
ALTER TABLE sectors
    ALTER COLUMN sector_time_seconds DROP NOT NULL;

-- Add is_valid column to sectors table
ALTER TABLE sectors
    ADD COLUMN is_valid BOOLEAN DEFAULT TRUE;

-- Add invalidation_reason column to sectors table
ALTER TABLE sectors
    ADD COLUMN invalidation_reason VARCHAR(100);

-- Add a constraint for reasonable sector times
ALTER TABLE sectors
    ADD CONSTRAINT reasonable_sector_time
        CHECK (sector_time_seconds IS NULL OR
               (sector_time_seconds > 0 AND sector_time_seconds < 3600));