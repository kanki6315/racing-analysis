package com.arjunakankipati.racingstatanalysis.repository;

import com.arjunakankipati.racingstatanalysis.model.Event;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Event entity operations.
 * Extends BaseRepository to inherit common CRUD operations.
 */
public interface EventRepository extends BaseRepository<Event, Long> {

    /**
     * Find events by series ID.
     *
     * @param seriesId the ID of the series to find events for
     * @return a list of events for the given series
     */
    List<Event> findBySeriesId(Long seriesId);

    /**
     * Find events by series ID and year.
     *
     * @param seriesId the ID of the series to find events for
     * @param year the year to find events for
     * @return a list of events for the given series and year
     */
    List<Event> findBySeriesIdAndYear(Long seriesId, Integer year);

    /**
     * Find an event by its name and year.
     *
     * @param name the name of the event to find
     * @param year the year of the event to find
     * @return an Optional containing the found event, or empty if not found
     */
    Optional<Event> findByNameAndYear(String name, Integer year);

    /**
     * Find all years for a given series.
     *
     * @param seriesId the ID of the series to find years for
     * @return a list of years for the given series
     */
    List<Integer> findYearsBySeriesId(Long seriesId);
}