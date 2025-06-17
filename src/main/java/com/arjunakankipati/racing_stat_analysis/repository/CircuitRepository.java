package com.arjunakankipati.racing_stat_analysis.repository;

import com.arjunakankipati.racing_stat_analysis.model.Circuit;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Circuit entity operations.
 * Extends BaseRepository to inherit common CRUD operations.
 */
public interface CircuitRepository extends BaseRepository<Circuit, Long> {

    /**
     * Find a circuit by its name.
     *
     * @param name the name of the circuit to find
     * @return an Optional containing the found circuit, or empty if not found
     */
    Optional<Circuit> findByName(String name);

    /**
     * Find circuits by country.
     *
     * @param country the country to find circuits for
     * @return a list of circuits in the given country
     */
    List<Circuit> findByCountry(String country);

    /**
     * Find circuits by name containing the given string.
     *
     * @param nameContains the string to search for in circuit names
     * @return a list of circuits with names containing the given string
     */
    List<Circuit> findByNameContaining(String nameContains);
}