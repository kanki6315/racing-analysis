package com.arjunakankipati.racingstatanalysis.repository;

import com.arjunakankipati.racingstatanalysis.model.Class;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Class entity operations.
 * Extends BaseRepository to inherit common CRUD operations.
 */
public interface ClassRepository extends BaseRepository<Class, Long> {

    /**
     * Find a class by its name.
     *
     * @param name the name of the class to find
     * @return an Optional containing the found class, or empty if not found
     */
    Optional<Class> findByName(String name);

    /**
     * Find classes by series ID.
     *
     * @param seriesId the ID of the series to find classes for
     * @return a list of classes for the given series
     */
    List<Class> findBySeriesId(Long seriesId);

    /**
     * Find a class by series ID and name.
     *
     * @param seriesId the ID of the series to find a class for
     * @param name the name of the class to find
     * @return an Optional containing the found class, or empty if not found
     */
    Optional<Class> findBySeriesIdAndName(Long seriesId, String name);

    /**
     * Find classes that competed in a specific event.
     * This is done by finding all sessions for the event, then all cars in those sessions,
     * and finally getting the unique classes from those cars.
     *
     * @param eventId the ID of the event to find classes for
     * @return a list of classes that competed in the event
     */
    List<Class> findByEventId(Long eventId);
}