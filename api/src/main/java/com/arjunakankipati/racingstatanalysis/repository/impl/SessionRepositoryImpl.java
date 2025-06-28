package com.arjunakankipati.racingstatanalysis.repository.impl;

import com.arjunakankipati.racingstatanalysis.jooq.Tables;
import com.arjunakankipati.racingstatanalysis.model.Session;
import com.arjunakankipati.racingstatanalysis.repository.SessionRepository;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

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
        super(dsl, Tables.SESSIONS, Tables.SESSIONS.ID);
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
                sessionRec.getName(),
                sessionRec.getType(),
                sessionRec.getStartDatetime(),
                sessionRec.getDurationSeconds()
        );
    }

    @Override
    protected Session insert(Session session) {
        Record record = dsl.insertInto(table)
                .columns(
                        Tables.SESSIONS.EVENT_ID,
                        Tables.SESSIONS.NAME,
                        Tables.SESSIONS.TYPE,
                        Tables.SESSIONS.START_DATETIME,
                        Tables.SESSIONS.DURATION_SECONDS
                )
                .values(
                        session.getEventId(),
                        session.getName(),
                        session.getType(),
                        session.getStartDatetime(),
                        session.getDurationSeconds()
                )
                .returning()
                .fetchOne();

        return mapToEntity(record);
    }

    @Override
    protected void update(Session session) {
        dsl.update(table)
                .set(Tables.SESSIONS.EVENT_ID, session.getEventId())
                .set(Tables.SESSIONS.NAME, session.getName())
                .set(Tables.SESSIONS.TYPE, session.getType())
                .set(Tables.SESSIONS.START_DATETIME, session.getStartDatetime())
                .set(Tables.SESSIONS.DURATION_SECONDS, session.getDurationSeconds())
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
}
