package com.arjunakankipati.racingstatanalysis.repository;

import com.arjunakankipati.racingstatanalysis.model.CarEntry;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for CarEntry entity operations.
 * Extends BaseRepository to inherit common CRUD operations.
 */
public interface CarEntryRepository extends BaseRepository<CarEntry, Long> {

    /**
     * Find car entries by session ID.
     *
     * @param sessionId the ID of the session to find car entries for
     * @return a list of car entries for the given session
     */
    List<CarEntry> findBySessionId(Long sessionId);

    /**
     * Find car entries by team ID.
     *
     * @param teamId the ID of the team to find car entries for
     * @return a list of car entries for the given team
     */
    List<CarEntry> findByTeamId(Long teamId);

    /**
     * Find car entries by class ID.
     *
     * @param classId the ID of the class to find car entries for
     * @return a list of car entries for the given class
     */
    List<CarEntry> findByClassId(Long classId);

    /**
     * Find car entries by car model ID.
     *
     * @param carModelId the ID of the car model to find car entries for
     * @return a list of car entries for the given car model
     */
    List<CarEntry> findByCarModelId(Long carModelId);

    /**
     * Find car entries by session ID and class ID.
     *
     * @param sessionId the ID of the session
     * @param classId   the ID of the class
     * @return a list of car entries for the given session and class
     */
    List<CarEntry> findBySessionIdAndClassId(Long sessionId, Long classId);

    /**
     * Find car entries by event ID and class ID.
     * This is done by finding all sessions for the event, then all car entries in those sessions
     * that belong to the specified class.
     *
     * @param eventId the ID of the event to find car entries for
     * @param classId the ID of the class to find car entries for
     * @return a list of car entries in the specified class for the event
     */
    List<CarEntry> findByEventIdAndClassId(Long eventId, Long classId);

    /**
     * Find a car entry by session ID and car number.
     *
     * @param sessionId the ID of the session
     * @param number    the car number
     * @return an Optional containing the found car entry, or empty if not found
     */
    Optional<CarEntry> findBySessionIdAndNumber(Long sessionId, String number);

    /**
     * Find car entries by tire supplier.
     *
     * @param tireSupplier the tire supplier to find car entries for
     * @return a list of car entries with the given tire supplier
     */
    List<CarEntry> findByTireSupplier(String tireSupplier);
} 