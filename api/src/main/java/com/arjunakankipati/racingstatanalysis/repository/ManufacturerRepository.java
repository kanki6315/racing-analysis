package com.arjunakankipati.racingstatanalysis.repository;

import com.arjunakankipati.racingstatanalysis.model.Manufacturer;

import java.util.List;
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

    /**
     * Find manufacturers by country.
     *
     * @param country the country to find manufacturers for
     * @return a list of manufacturers from the given country
     */
    List<Manufacturer> findByCountry(String country);

    /**
     * Find manufacturers by name containing the given string.
     *
     * @param nameContains the string to search for in manufacturer names
     * @return a list of manufacturers with names containing the given string
     */
    List<Manufacturer> findByNameContaining(String nameContains);
}