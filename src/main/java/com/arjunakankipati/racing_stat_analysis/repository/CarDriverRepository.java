package com.arjunakankipati.racing_stat_analysis.repository;

import com.arjunakankipati.racing_stat_analysis.model.CarDriver;

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
     * Find car drivers by driver ID.
     *
     * @param driverId the ID of the driver to find car associations for
     * @return a list of car drivers for the given driver
     */
    List<CarDriver> findByDriverId(Long driverId);

    /**
     * Find a car driver by car ID and driver ID.
     *
     * @param carId the ID of the car
     * @param driverId the ID of the driver
     * @return an Optional containing the found car driver, or empty if not found
     */
    Optional<CarDriver> findByCarIdAndDriverId(Long carId, Long driverId);

    /**
     * Find car drivers by driver number.
     *
     * @param driverNumber the driver number to find car drivers for
     * @return a list of car drivers with the given driver number
     */
    List<CarDriver> findByDriverNumber(Integer driverNumber);

    /**
     * Find car drivers by car ID and driver number.
     *
     * @param carId the ID of the car
     * @param driverNumber the driver number
     * @return an Optional containing the found car driver, or empty if not found
     */
    Optional<CarDriver> findByCarIdAndDriverNumber(Long carId, Integer driverNumber);
}