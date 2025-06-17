package com.arjunakankipati.racing_stat_analysis.repository;

import com.arjunakankipati.racing_stat_analysis.model.Driver;

import java.util.List;
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

    /**
     * Find drivers by nationality.
     *
     * @param nationality the nationality to find drivers for
     * @return a list of drivers with the given nationality
     */
    List<Driver> findByNationality(String nationality);

    /**
     * Find drivers by license type.
     *
     * @param licenseType the license type to find drivers for
     * @return a list of drivers with the given license type
     */
    List<Driver> findByLicenseType(String licenseType);

    /**
     * Find a driver by external ID.
     *
     * @param externalId the external ID to find a driver for
     * @return an Optional containing the found driver, or empty if not found
     */
    Optional<Driver> findByExternalId(String externalId);

    /**
     * Find drivers by first name or last name containing the given string.
     *
     * @param nameContains the string to search for in driver names
     * @return a list of drivers with first or last names containing the given string
     */
    List<Driver> findByFirstNameContainingOrLastNameContaining(String nameContains, String nameContains2);

    /**
     * Find drivers by hometown containing the given string.
     *
     * @param hometownContains the string to search for in driver hometowns
     * @return a list of drivers with hometowns containing the given string
     */
    List<Driver> findByHometownContaining(String hometownContains);
}