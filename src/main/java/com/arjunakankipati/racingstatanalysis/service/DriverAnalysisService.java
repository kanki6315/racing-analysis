package com.arjunakankipati.racingstatanalysis.service;

import com.arjunakankipati.racingstatanalysis.dto.LapTimeDTO;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for driver analysis operations.
 */
public interface DriverAnalysisService {

    /**
     * Gets the top lap times for a driver.
     *
     * @param driverId the ID of the driver
     * @param percentage the percentage of top lap times to return (e.g., 20 for top 20%)
     * @param seriesId optional filter by series ID
     * @param year optional filter by year
     * @param eventId optional filter by event ID
     * @param sessionId optional filter by session ID
     * @return a list of top lap times for the driver
     * @throws UnsupportedOperationException if the operation is not implemented
     */
    List<LapTimeDTO> getTopLapTimes(Long driverId, int percentage, 
                                   Optional<Long> seriesId, 
                                   Optional<Integer> year, 
                                   Optional<Long> eventId,
                                   Optional<Long> sessionId);
}