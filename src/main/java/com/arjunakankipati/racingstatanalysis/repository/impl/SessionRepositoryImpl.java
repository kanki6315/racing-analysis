package com.arjunakankipati.racingstatanalysis.repository.impl;

import com.arjunakankipati.racingstatanalysis.jooq.Tables;
import com.arjunakankipati.racingstatanalysis.model.Session;
import com.arjunakankipati.racingstatanalysis.repository.SessionRepository;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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

        var sessionRec = record.into(Tables.SESSIONS);
        return new Session(
                sessionRec.getId(),
                sessionRec.getEventId(),
                sessionRec.getCircuitId(),
                sessionRec.getName(),
                sessionRec.getType(),
                sessionRec.getStartDatetime(),
                sessionRec.getDurationSeconds(),
                sessionRec.getWeatherAirTemp(),
                sessionRec.getWeatherTrackTemp(),
                sessionRec.getWeatherCondition(),
                sessionRec.getReportMessage(),
                sessionRec.getImportUrl(),
                sessionRec.getImportTimestamp()
        );
    }

    @Override
    protected Session insert(Session session) {
        Record record = dsl.insertInto(table)
                .columns(
                        Tables.SESSIONS.EVENT_ID,
                        Tables.SESSIONS.CIRCUIT_ID,
                        Tables.SESSIONS.NAME,
                        Tables.SESSIONS.TYPE,
                        Tables.SESSIONS.START_DATETIME,
                        Tables.SESSIONS.DURATION_SECONDS,
                        Tables.SESSIONS.WEATHER_AIR_TEMP,
                        Tables.SESSIONS.WEATHER_TRACK_TEMP,
                        Tables.SESSIONS.WEATHER_CONDITION,
                        Tables.SESSIONS.REPORT_MESSAGE,
                        Tables.SESSIONS.IMPORT_URL,
                        Tables.SESSIONS.IMPORT_TIMESTAMP
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
                .returning()
                .fetchOne();

        return mapToEntity(record);
    }

    @Override
    protected void update(Session session) {
        dsl.update(table)
                .set(Tables.SESSIONS.EVENT_ID, session.getEventId())
                .set(Tables.SESSIONS.CIRCUIT_ID, session.getCircuitId())
                .set(Tables.SESSIONS.NAME, session.getName())
                .set(Tables.SESSIONS.TYPE, session.getType())
                .set(Tables.SESSIONS.START_DATETIME, session.getStartDatetime())
                .set(Tables.SESSIONS.DURATION_SECONDS, session.getDurationSeconds())
                .set(Tables.SESSIONS.WEATHER_AIR_TEMP, session.getWeatherAirTemp())
                .set(Tables.SESSIONS.WEATHER_TRACK_TEMP, session.getWeatherTrackTemp())
                .set(Tables.SESSIONS.WEATHER_CONDITION, session.getWeatherCondition())
                .set(Tables.SESSIONS.REPORT_MESSAGE, session.getReportMessage())
                .set(Tables.SESSIONS.IMPORT_URL, session.getImportUrl())
                .set(Tables.SESSIONS.IMPORT_TIMESTAMP, session.getImportTimestamp())
                .where(idField.eq(session.getId()))
                .execute();
    }

    @Override
    public List<Session> findByEventId(Long eventId) {
        return dsl.select()
                .from(table)
                .where(Tables.SESSIONS.EVENT_ID.eq(eventId))
                .fetch()
                .map(this::mapToEntity);
    }

    @Override
    public List<Session> findByCircuitId(Long circuitId) {
        return dsl.select()
                .from(table)
                .where(Tables.SESSIONS.CIRCUIT_ID.eq(circuitId))
                .fetch()
                .map(this::mapToEntity);
    }

    @Override
    public List<Session> findByType(String type) {
        return dsl.select()
                .from(table)
                .where(Tables.SESSIONS.TYPE.eq(type))
                .fetch()
                .map(this::mapToEntity);
    }

    @Override
    public List<Session> findByEventIdAndType(Long eventId, String type) {
        return dsl.select()
                .from(table)
                .where(Tables.SESSIONS.EVENT_ID.eq(eventId))
                .and(Tables.SESSIONS.TYPE.eq(type))
                .fetch()
                .map(this::mapToEntity);
    }

    @Override
    public List<Session> findByStartDatetimeBetween(LocalDateTime startFrom, LocalDateTime startTo) {
        return dsl.select()
                .from(table)
                .where(Tables.SESSIONS.START_DATETIME.ge(startFrom))
                .and(Tables.SESSIONS.START_DATETIME.le(startTo))
                .fetch()
                .map(this::mapToEntity);
    }

    @Override
    public Optional<Session> findByImportUrl(String importUrl) {
        Record record = dsl.select()
                .from(table)
                .where(Tables.SESSIONS.IMPORT_URL.eq(importUrl))
                .fetchOne();

        return Optional.ofNullable(record)
                .map(this::mapToEntity);
    }
}
