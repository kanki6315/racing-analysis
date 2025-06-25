-- V5 Migration: Add Performance Indexes for SeriesController Endpoints
-- This migration adds strategic indexes to optimize the most common query patterns
-- used by the SeriesController endpoints while minimizing impact on write performance.

-- Index 1: Events by series_id and year (for /{seriesId}/years and /{seriesId}/{year}/events endpoints)
-- This composite index optimizes both findBySeriesId and findBySeriesIdAndYear queries
-- Also covers event counting for getAllSeries endpoint
CREATE INDEX idx_events_series_id_year ON events (series_id, year);

-- Index 2: Sessions by event_id (for /events/{eventId}/sessions endpoint)
-- This is the most common query pattern for sessions and is used in many complex joins
CREATE INDEX idx_sessions_event_id ON sessions (event_id);

-- Index 3: Car entries by session_id and class_id (for /events/{eventId}/classes/{classId}/cars endpoint)
-- This optimizes the complex join query in findByEventIdAndClassId and similar patterns
CREATE INDEX idx_car_entries_session_class ON car_entries (session_id, class_id);

-- Index 4: Laps by car_id, is_valid, and lap_time_seconds (for lap time analysis queries)
-- This composite index optimizes the most complex and performance-critical lap time analysis
-- Covers both overall and driver-specific lap time analysis with ordering
CREATE INDEX idx_laps_car_valid_time ON laps (car_id, is_valid, lap_time_seconds);

-- Index 5: Laps by driver_id, is_valid, and lap_time_seconds (for driver-specific lap analysis)
-- This optimizes driver-specific lap time analysis which is heavily used in the lap time analysis endpoint
CREATE INDEX idx_laps_driver_valid_time ON laps (driver_id, is_valid, lap_time_seconds);

-- Index 6: Car entries by team_id (for team queries that join with car_entries)
-- This optimizes the complex team queries that involve car entry relationships
CREATE INDEX idx_car_entries_team_id ON car_entries (team_id);

-- Index 7: Car drivers by car_id (for team queries that join with car_drivers)
-- This optimizes the complex team queries that involve car driver relationships
CREATE INDEX idx_car_drivers_car_id ON car_drivers (car_id);

-- Index 8: Classes by series_id (for series-based class queries)
-- This optimizes queries that find classes by series, used in various filtering scenarios
CREATE INDEX idx_classes_series_id ON classes (series_id); 