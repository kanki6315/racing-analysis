CREATE TABLE import_jobs
(
    id         SERIAL PRIMARY KEY,
    status     VARCHAR(32) NOT NULL,
    created_at TIMESTAMP   NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP   NOT NULL DEFAULT NOW(),
    started_at TIMESTAMP NULL,
    ended_at   TIMESTAMP NULL,
    error      TEXT NULL,
    source_url TEXT NULL
);

-- Optionally, add an index on status for faster queries by status
CREATE INDEX idx_import_jobs_status ON import_jobs (status);