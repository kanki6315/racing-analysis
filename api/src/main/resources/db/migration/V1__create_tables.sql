CREATE TABLE series (
                        id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name VARCHAR(255) NOT NULL,
    description TEXT
);

CREATE TABLE events (
                        id        BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
                        series_id BIGINT REFERENCES series (id),
    name VARCHAR(255) NOT NULL,
    year INTEGER NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    description TEXT
);

CREATE TABLE circuits (
                          id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name VARCHAR(255) NOT NULL,
    length_meters DECIMAL(10,3) NOT NULL,
    country VARCHAR(100) NOT NULL,
    location VARCHAR(255),
    description TEXT
);

CREATE TABLE sessions (
                          id         BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
                          event_id   BIGINT REFERENCES events (id),
                          circuit_id BIGINT REFERENCES circuits (id),
    name VARCHAR(255) NOT NULL,
    type VARCHAR(50) NOT NULL,
    start_datetime TIMESTAMP NOT NULL,
    duration_seconds INTEGER NOT NULL,
    weather_air_temp DECIMAL(5,2),
    weather_track_temp DECIMAL(5,2),
    weather_condition VARCHAR(50),
    report_message TEXT,
    import_url VARCHAR(2048),
    import_timestamp TIMESTAMP
);

CREATE TABLE teams (
                       id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name VARCHAR(255) NOT NULL,
    description TEXT
);

CREATE TABLE manufacturers (
                               id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name VARCHAR(255) NOT NULL,
    country VARCHAR(100)
);

CREATE TABLE classes (
                         id        BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
                         series_id BIGINT REFERENCES series (id),
    name VARCHAR(100) NOT NULL,
    description TEXT
);

CREATE TABLE drivers (
                         id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    nationality VARCHAR(100),
    hometown VARCHAR(255),
    license_type VARCHAR(50),
    external_id VARCHAR(100)
);

CREATE TABLE cars (
                      id              BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
                      session_id      BIGINT REFERENCES sessions (id),
                      team_id         BIGINT REFERENCES teams (id),
                      class_id        BIGINT REFERENCES classes (id),
                      manufacturer_id BIGINT REFERENCES manufacturers (id),
    number VARCHAR(10) NOT NULL,
    model VARCHAR(255) NOT NULL,
    tire_supplier VARCHAR(50)
);

CREATE TABLE car_drivers (
                             id        BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
                             car_id    BIGINT REFERENCES cars (id),
                             driver_id BIGINT REFERENCES drivers (id),
    driver_number INTEGER NOT NULL
);

CREATE TABLE laps (
                      id        BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
                      car_id    BIGINT REFERENCES cars (id),
                      driver_id BIGINT REFERENCES drivers (id),
    lap_number INTEGER NOT NULL,
    lap_time_seconds DECIMAL(10,3) NOT NULL,
    session_elapsed_seconds DECIMAL(12,3) NOT NULL,
    timestamp TIMESTAMP NOT NULL,
    average_speed_kph DECIMAL(7,3),
    is_valid BOOLEAN DEFAULT TRUE,
    is_personal_best BOOLEAN DEFAULT FALSE,
    is_session_best BOOLEAN DEFAULT FALSE,
    invalidation_reason VARCHAR(100)
);

CREATE TABLE sectors (
                         id                  BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
                         lap_id              BIGINT REFERENCES laps (id),
    sector_number INTEGER NOT NULL,
                         sector_time_seconds DECIMAL(12, 3) NOT NULL,
    is_personal_best BOOLEAN DEFAULT FALSE,
    is_session_best BOOLEAN DEFAULT FALSE
);