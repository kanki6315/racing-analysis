package com.arjunakankipati.racingstatanalysis.repository;

import com.arjunakankipati.racingstatanalysis.model.Session;

import java.util.List;

/**
 * Repository interface for Session entity operations.
 * Extends BaseRepository to inherit common CRUD operations.
 */
public interface SessionRepository extends BaseRepository<Session, Long> {

    /**
     * Find sessions by event ID.
     *
     * @param eventId the ID of the event to find sessions for
     * @return a list of sessions for the given event
     */
    List<Session> findByEventId(Long eventId);
}