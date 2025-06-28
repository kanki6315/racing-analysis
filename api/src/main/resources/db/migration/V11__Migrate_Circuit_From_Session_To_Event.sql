-- 1. Add circuit_id to events table
ALTER TABLE events
    ADD COLUMN circuit_id BIGINT;

-- 2. Migrate data: set event.circuit_id to the circuit_id of one of its sessions (if any)
UPDATE events e
SET circuit_id = (SELECT s.circuit_id
                  FROM sessions s
                  WHERE s.event_id = e.id
                    AND s.circuit_id IS NOT NULL
    LIMIT 1
    );

-- 3. Add foreign key constraint (optional, but recommended)
ALTER TABLE events
    ADD CONSTRAINT fk_events_circuit
        FOREIGN KEY (circuit_id) REFERENCES circuits (id);

-- 4. Remove circuit_id from sessions table
ALTER TABLE sessions DROP COLUMN circuit_id;