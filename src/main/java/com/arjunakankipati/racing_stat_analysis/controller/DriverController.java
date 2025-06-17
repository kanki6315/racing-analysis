package com.arjunakankipati.racing_stat_analysis.controller;

import com.arjunakankipati.racing_stat_analysis.dto.LapTimeDTO;
import com.arjunakankipati.racing_stat_analysis.dto.TopLapsResponseDTO;
import com.arjunakankipati.racing_stat_analysis.service.DriverAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for driver operations.
 */
@RestController
@RequestMapping("/api/v1/drivers")
public class DriverController {

    private final DriverAnalysisService driverAnalysisService;

    /**
     * Constructor with DriverAnalysisService dependency injection.
     *
     * @param driverAnalysisService the driver analysis service
     */
    @Autowired
    public DriverController(DriverAnalysisService driverAnalysisService) {
        this.driverAnalysisService = driverAnalysisService;
    }

    /**
     * Gets the top lap times for a driver.
     *
     * @param driverId the ID of the driver
     * @param percentage the percentage of top lap times to return (default: 20)
     * @param seriesId optional filter by series ID
     * @param year optional filter by year
     * @param eventId optional filter by event ID
     * @param sessionId optional filter by session ID
     * @return a response entity containing the top lap times for the driver
     */
    @GetMapping("/{driverId}/top-laps")
    public ResponseEntity<TopLapsResponseDTO> getTopLaps(
            @PathVariable Long driverId,
            @RequestParam(defaultValue = "20") int percentage,
            @RequestParam(required = false) Long seriesId,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Long eventId,
            @RequestParam(required = false) Long sessionId) {
        
        List<LapTimeDTO> topLaps = driverAnalysisService.getTopLapTimes(
            driverId, percentage, Optional.ofNullable(seriesId),
            Optional.ofNullable(year), Optional.ofNullable(eventId),
            Optional.ofNullable(sessionId));
        
        TopLapsResponseDTO response = new TopLapsResponseDTO(driverId, topLaps);
        return ResponseEntity.ok(response);
    }
}