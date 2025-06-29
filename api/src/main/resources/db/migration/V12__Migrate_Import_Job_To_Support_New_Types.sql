-- Drop the old source_url column
ALTER TABLE import_jobs DROP COLUMN IF EXISTS source_url;

-- Add the new url, import_type, and process_type columns
ALTER TABLE import_jobs
    ADD COLUMN url text;
ALTER TABLE import_jobs
    ADD COLUMN import_type VARCHAR(32);
ALTER TABLE import_jobs
    ADD COLUMN process_type VARCHAR(32);

-- Add the new session_id column with a foreign key to sessions(id)
ALTER TABLE import_jobs
    ADD COLUMN session_id BIGINT REFERENCES sessions (id);
