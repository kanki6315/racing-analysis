package com.arjunakankipati.racingstatanalysis.repository;

import com.arjunakankipati.racingstatanalysis.dto.DriverLapTimeAnalysisDTO;
import com.arjunakankipati.racingstatanalysis.dto.DriverLapTimesDTO;
import com.arjunakankipati.racingstatanalysis.dto.LapTimeAnalysisDTO;
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

    /**
     * Find top percentage of laps by driver ID, validity, and optional filters for session, event, year, and series.
     * This method uses SQL JOINs to filter laps in the database rather than in memory.
     * The laps are sorted by lap time (ascending) and limited to the top percentage.
     *
     * @param driverId   the ID of the driver to find laps for
     * @param isValid    whether to find only valid laps
     * @param percentage the percentage of top laps to return (e.g., 20 for top 20%)
     * @param sessionId  optional filter by session ID
     * @param eventId    optional filter by event ID
     * @param year       optional filter by year
     * @param seriesId   optional filter by series ID
     * @return a list of top percentage laps for the driver
     */
    List<Lap> findTopPercentageLapsByDriverId(Long driverId, boolean isValid, int percentage,
                                              Optional<Long> sessionId,
                                              Optional<Long> eventId,
                                              Optional<Integer> year,
                                              Optional<Long> seriesId);

    /**
     * Calculate lap time analysis for an event with optional filters.
     * This method uses SQL to calculate the average of the top percentage of valid lap times,
     * the fastest lap time, the median lap time, and the total lap count.
     *
     * @param eventId    the ID of the event
     * @param percentage the percentage of top laps to include in the average calculation (default: 20)
     * @param classId    optional filter by class ID
     * @param carId      optional filter by car model ID
     * @param sessionId  optional filter by session ID
     * @param offset     optional pagination offset
     * @param limit      optional pagination limit
     * @return a DTO containing the lap time analysis
     */
    LapTimeAnalysisDTO calculateLapTimeAnalysisForEvent(
            Long eventId,
            int percentage,
            Optional<Long> classId,
            Optional<Long> carId,
            Optional<Long> sessionId,
            Optional<Integer> offset,
            Optional<Integer> limit);

    /**
     * Calculate lap time analysis per driver for an event with optional filters.
     * This method uses SQL to calculate the average of the top percentage of valid lap times,
     * the fastest lap time, the median lap time, and the total lap count for each driver.
     * Results are ordered by the average lap time for paging purposes.
     *
     * @param eventId    the ID of the event
     * @param percentage the percentage of top laps to include in the average calculation (default: 20)
     * @param classId    optional filter by class ID
     * @param carId      optional filter by car model ID
     * @param sessionId  optional filter by session ID
     * @param offset     optional pagination offset
     * @param limit      optional pagination limit
     * @return a list of DTOs containing driver-specific lap time analysis
     */
    List<DriverLapTimeAnalysisDTO> calculateLapTimeAnalysisPerDriverForEvent(
            Long eventId,
            int percentage,
            Optional<Long> classId,
            Optional<Long> carId,
            Optional<Long> sessionId,
            Optional<Integer> offset,
            Optional<Integer> limit);

    /**
     * Find lap times for multiple drivers in a specific session.
     * This method uses SQL JOINs to efficiently retrieve lap times with driver, car, team, and class information.
     *
     * @param sessionId the ID of the session
     * @param driverIds the list of driver IDs to find lap times for
     * @return a list of DTOs containing driver-specific lap times
     */
    List<DriverLapTimesDTO> findLapTimesForDriversInSession(Long sessionId, List<Long> driverIds);

    /**
     * Batch insert laps.
     * @param laps the list of laps to insert
     * @return the list of inserted laps (with IDs populated)
     */
    List<Lap> saveAll(List<Lap> laps);
}
