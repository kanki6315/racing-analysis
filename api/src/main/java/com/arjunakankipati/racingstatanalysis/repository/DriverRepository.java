package com.arjunakankipati.racingstatanalysis.repository;

import com.arjunakankipati.racingstatanalysis.model.Driver;

import java.util.Optional;

/**
 * Repository interface for Driver entity operations.
 * Extends BaseRepository to inherit common CRUD operations.
 */
public interface DriverRepository extends BaseRepository<Driver, Long> {

    /**
     * Find a driver by first name and last name.
     *
     * @param firstName the first name of the driver to find
     * @param lastName the last name of the driver to find
     * @return an Optional containing the found driver, or empty if not found
     */
    Optional<Driver> findByFirstNameAndLastName(String firstName, String lastName);
}