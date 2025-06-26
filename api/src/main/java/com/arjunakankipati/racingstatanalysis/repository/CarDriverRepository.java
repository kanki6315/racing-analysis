package com.arjunakankipati.racingstatanalysis.repository;

import com.arjunakankipati.racingstatanalysis.model.CarDriver;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for CarDriver entity operations.
 * Extends BaseRepository to inherit common CRUD operations.
 */
public interface CarDriverRepository extends BaseRepository<CarDriver, Long> {

    /**
     * Find car drivers by car ID.
     *
     * @param carId the ID of the car to find drivers for
     * @return a list of car drivers for the given car
     */
    List<CarDriver> findByCarId(Long carId);

    /**
     * Find a car driver by car ID and driver ID.
     *
     * @param carId the ID of the car
     * @param driverId the ID of the driver
     * @return an Optional containing the found car driver, or empty if not found
     */
    Optional<CarDriver> findByCarIdAndDriverId(Long carId, Long driverId);
}