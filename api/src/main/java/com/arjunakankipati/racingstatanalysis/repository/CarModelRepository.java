package com.arjunakankipati.racingstatanalysis.repository;

import com.arjunakankipati.racingstatanalysis.model.CarModel;

import java.util.Optional;

/**
 * Repository interface for CarModel entity operations.
 * Extends BaseRepository to inherit common CRUD operations.
 */
public interface CarModelRepository extends BaseRepository<CarModel, Long> {
    /**
     * Find a car model by name.
     *
     * @param name the name of the car model
     * @return an Optional containing the found car model, or empty if not found
     */
    Optional<CarModel> findByName(String name);
}