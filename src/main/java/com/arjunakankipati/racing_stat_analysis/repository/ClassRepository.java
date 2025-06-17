package com.arjunakankipati.racing_stat_analysis.repository;

import com.arjunakankipati.racing_stat_analysis.model.Class;

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
     * Find classes by name containing the given string.
     *
     * @param nameContains the string to search for in class names
     * @return a list of classes with names containing the given string
     */
    List<Class> findByNameContaining(String nameContains);
}