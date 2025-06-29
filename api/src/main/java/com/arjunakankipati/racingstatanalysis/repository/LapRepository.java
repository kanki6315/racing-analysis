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
