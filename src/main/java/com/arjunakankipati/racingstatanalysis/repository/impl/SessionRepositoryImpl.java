package com.arjunakankipati.racingstatanalysis.repository.impl;

import com.arjunakankipati.racingstatanalysis.model.Session;
import com.arjunakankipati.racingstatanalysis.repository.SessionRepository;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.jooq.impl.DSL.field;

/**
 * Implementation of the SessionRepository interface using JOOQ.
 * Extends BaseRepositoryImpl to inherit common CRUD operations.
 */
@Repository
public class SessionRepositoryImpl extends BaseRepositoryImpl<Session, Long> implements SessionRepository {

    /**
     * Constructor with DSLContext dependency injection.
     *
     * @param dsl the JOOQ DSL context
     */
    @Autowired
    public SessionRepositoryImpl(DSLContext dsl) {
        super(dsl, "sessions", "id", Long.class);
    }

    @Override
    protected Session mapToEntity(Record record) {
        if (record == null) {
            return null;
        }

        return new Session(
                record.get(field("id", Long.class)),
                record.get(field("event_id", Long.class)),
                record.get(field("circuit_id", Long.class)),
                record.get(field("name", String.class)),
                record.get(field("type", String.class)),
                record.get(field("start_datetime", Timestamp.class)).toLocalDateTime(),
                record.get(field("duration_seconds", Integer.class)),
                record.get(field("weather_air_temp", BigDecimal.class)),
                record.get(field("weather_track_temp", BigDecimal.class)),
                record.get(field("weather_condition", String.class)),
                record.get(field("report_message", String.class)),
                record.get(field("import_url", String.class)),
                record.get(field("import_timestamp", Timestamp.class)).toLocalDateTime()
        );
    }

    @Override
    protected Session insert(Session session) {
        Record record = dsl.insertInto(table)
                .columns(
                        field("event_id"),
                        field("circuit_id"),
                        field("name"),
                        field("type"),
                        field("start_datetime"),
                        field("duration_seconds"),
                        field("weather_air_temp"),
                        field("weather_track_temp"),
                        field("weather_condition"),
                        field("report_message"),
                        field("import_url"),
                        field("import_timestamp")
                )
                .values(
                        session.getEventId(),
                        session.getCircuitId(),
                        session.getName(),
                        session.getType(),
                        session.getStartDatetime(),
                        session.getDurationSeconds(),
                        session.getWeatherAirTemp(),
                        session.getWeatherTrackTemp(),
                        session.getWeatherCondition(),
                        session.getReportMessage(),
                        session.getImportUrl(),
                        session.getImportTimestamp()
                )
                .returningResult(
                        field("id"),
                        field("event_id"),
                        field("circuit_id"),
                        field("name"),
                        field("type"),
                        field("start_datetime"),
                        field("duration_seconds"),
                        field("weather_air_temp"),
                        field("weather_track_temp"),
                        field("weather_condition"),
                        field("report_message"),
                        field("import_url"),
                        field("import_timestamp")
                )
                .fetchOne();

        return mapToEntity(record);
    }

    @Override
    protected void update(Session session) {
        dsl.update(table)
                .set(field("event_id"), session.getEventId())
                .set(field("circuit_id"), session.getCircuitId())
                .set(field("name"), session.getName())
                .set(field("type"), session.getType())
                .set(field("start_datetime"), session.getStartDatetime())
                .set(field("duration_seconds"), session.getDurationSeconds())
                .set(field("weather_air_temp"), session.getWeatherAirTemp())
                .set(field("weather_track_temp"), session.getWeatherTrackTemp())
                .set(field("weather_condition"), session.getWeatherCondition())
                .set(field("report_message"), session.getReportMessage())
                .set(field("import_url"), session.getImportUrl())
                .set(field("import_timestamp"), session.getImportTimestamp())
                .where(idField.eq(session.getId()))
                .execute();
    }

    @Override
    public List<Session> findByEventId(Long eventId) {
        return dsl.select()
                .from(table)
                .where(field("event_id").eq(eventId))
                .fetch()
                .map(this::mapToEntity);
    }

    @Override
    public List<Session> findByCircuitId(Long circuitId) {
        return dsl.select()
                .from(table)
                .where(field("circuit_id").eq(circuitId))
                .fetch()
                .map(this::mapToEntity);
    }

    @Override
    public List<Session> findByType(String type) {
        return dsl.select()
                .from(table)
                .where(field("type").eq(type))
                .fetch()
                .map(this::mapToEntity);
    }

    @Override
    public List<Session> findByEventIdAndType(Long eventId, String type) {
        return dsl.select()
                .from(table)
                .where(field("event_id").eq(eventId))
                .and(field("type").eq(type))
                .fetch()
                .map(this::mapToEntity);
    }

    @Override
    public List<Session> findByStartDatetimeBetween(LocalDateTime startFrom, LocalDateTime startTo) {
        return dsl.select()
                .from(table)
                .where(field("start_datetime").ge(startFrom))
                .and(field("start_datetime").le(startTo))
                .fetch()
                .map(this::mapToEntity);
    }

    @Override
    public Optional<Session> findByImportUrl(String importUrl) {
        Record record = dsl.select()
                .from(table)
                .where(field("import_url").eq(importUrl))
                .fetchOne();

        return Optional.ofNullable(record)
                .map(this::mapToEntity);
    }
}