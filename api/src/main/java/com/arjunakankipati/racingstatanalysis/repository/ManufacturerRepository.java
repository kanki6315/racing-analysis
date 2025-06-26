package com.arjunakankipati.racingstatanalysis.repository;

import com.arjunakankipati.racingstatanalysis.model.Manufacturer;

import java.util.Optional;

/**
 * Repository interface for Manufacturer entity operations.
 * Extends BaseRepository to inherit common CRUD operations.
 */
public interface ManufacturerRepository extends BaseRepository<Manufacturer, Long> {

    /**
     * Find a manufacturer by its name.
     *
     * @param name the name of the manufacturer to find
     * @return an Optional containing the found manufacturer, or empty if not found
     */
    Optional<Manufacturer> findByName(String name);
}