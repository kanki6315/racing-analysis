ALTER TABLE sessions
DROP
COLUMN IF EXISTS import_url,
DROP
COLUMN IF EXISTS import_timestamp;