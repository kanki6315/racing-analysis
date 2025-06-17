package com.arjunakankipati.racingstatanalysis.repository;

import com.arjunakankipati.racingstatanalysis.model.Lap;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Lap entity operations.
 * Extends BaseRepository to inherit common CRUD operations.
 */
public interface LapRepository extends BaseRepository<Lap, Long> {

    /**
     * Find laps by car ID.
     *
     * @param carId the ID of the car to find laps for
     * @return a list of laps for the given car
     */
    List<Lap> findByCarId(Long carId);

    /**
     * Find laps by driver ID.
     *
     * @param driverId the ID of the driver to find laps for
     * @return a list of laps for the given driver
     */
    List<Lap> findByDriverId(Long driverId);

    /**
     * Find a lap by car ID and lap number.
     *
     * @param carId the ID of the car
     * @param lapNumber the lap number
     * @return an Optional containing the found lap, or empty if not found
     */
    Optional<Lap> findByCarIdAndLapNumber(Long carId, Integer lapNumber);

    /**
     * Find valid laps by car ID.
     *
     * @param carId the ID of the car to find valid laps for
     * @return a list of valid laps for the given car
     */
    List<Lap> findByCarIdAndIsValidTrue(Long carId);

    /**
     * Find valid laps by driver ID.
     *
     * @param driverId the ID of the driver to find valid laps for
     * @return a list of valid laps for the given driver
     */
    List<Lap> findByDriverIdAndIsValidTrue(Long driverId);

    /**
     * Find personal best laps by car ID.
     *
     * @param carId the ID of the car to find personal best laps for
     * @return a list of personal best laps for the given car
     */
    List<Lap> findByCarIdAndIsPersonalBestTrue(Long carId);

    /**
     * Find session best laps by car ID.
     *
     * @param carId the ID of the car to find session best laps for
     * @return a list of session best laps for the given car
     */
    List<Lap> findByCarIdAndIsSessionBestTrue(Long carId);

    /**
     * Find top N laps by car ID ordered by lap time.
     *
     * @param carId the ID of the car to find top laps for
     * @param limit the number of top laps to return
     * @return a list of top laps for the given car
     */
    List<Lap> findTopLapsByCarId(Long carId, int limit);

    /**
     * Find top N laps by driver ID ordered by lap time.
     *
     * @param driverId the ID of the driver to find top laps for
     * @param limit the number of top laps to return
     * @return a list of top laps for the given driver
     */
    List<Lap> findTopLapsByDriverId(Long driverId, int limit);

    /**
     * Find filtered laps by driver ID, validity, and optional filters for session, event, year, and series.
     * This method uses SQL JOINs to filter laps in the database rather than in memory.
     *
     * @param driverId  the ID of the driver to find laps for
     * @param isValid   whether to find only valid laps
     * @param sessionId optional filter by session ID
     * @param eventId   optional filter by event ID
     * @param year      optional filter by year
     * @param seriesId  optional filter by series ID
     * @return a list of filtered laps
     */
    List<Lap> findFilteredLaps(Long driverId, boolean isValid,
                               Optional<Long> sessionId,
                               Optional<Long> eventId,
                               Optional<Integer> year,
                               Optional<Long> seriesId);
}
