package com.arjunakankipati.racing_stat_analysis.repository;

import com.arjunakankipati.racing_stat_analysis.model.Session;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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

    /**
     * Find sessions by circuit ID.
     *
     * @param circuitId the ID of the circuit to find sessions for
     * @return a list of sessions for the given circuit
     */
    List<Session> findByCircuitId(Long circuitId);

    /**
     * Find sessions by type.
     *
     * @param type the type of sessions to find
     * @return a list of sessions of the given type
     */
    List<Session> findByType(String type);

    /**
     * Find sessions by event ID and type.
     *
     * @param eventId the ID of the event to find sessions for
     * @param type the type of sessions to find
     * @return a list of sessions for the given event and of the given type
     */
    List<Session> findByEventIdAndType(Long eventId, String type);

    /**
     * Find sessions by start date range.
     *
     * @param startFrom the start of the date range
     * @param startTo the end of the date range
     * @return a list of sessions starting within the given date range
     */
    List<Session> findByStartDatetimeBetween(LocalDateTime startFrom, LocalDateTime startTo);

    /**
     * Find a session by its import URL.
     *
     * @param importUrl the import URL to find a session for
     * @return an Optional containing the found session, or empty if not found
     */
    Optional<Session> findByImportUrl(String importUrl);
}