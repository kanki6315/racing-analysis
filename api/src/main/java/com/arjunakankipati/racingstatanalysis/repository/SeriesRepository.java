package com.arjunakankipati.racingstatanalysis.repository;

import com.arjunakankipati.racingstatanalysis.model.Series;

import java.util.Optional;

/**
 * Repository interface for Series entity operations.
 * Extends BaseRepository to inherit common CRUD operations.
 */
public interface SeriesRepository extends BaseRepository<Series, Long> {

    /**
     * Find a series by its name.
     *
     * @param name the name of the series to find
     * @return an Optional containing the found series, or empty if not found
     */
    Optional<Series> findByName(String name);
}
