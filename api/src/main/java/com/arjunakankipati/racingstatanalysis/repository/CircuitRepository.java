package com.arjunakankipati.racingstatanalysis.repository;

import com.arjunakankipati.racingstatanalysis.model.Circuit;

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
}