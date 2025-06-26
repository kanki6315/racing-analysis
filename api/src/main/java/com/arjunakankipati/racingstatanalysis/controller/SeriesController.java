package com.arjunakankipati.racingstatanalysis.controller;

import com.arjunakankipati.racingstatanalysis.dto.*;
import com.arjunakankipati.racingstatanalysis.repository.LapRepository;
import com.arjunakankipati.racingstatanalysis.service.SeriesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for series operations.
 */
@RestController
@RequestMapping("/api/v1/series")
public class SeriesController {

    private final SeriesService seriesService;
    private final LapRepository lapRepository;

    @Value("${api.key}")
    private String expectedApiKey;

    /**
     * Constructor with SeriesService and LapRepository dependency injection.
     *
     * @param seriesService the series service
     * @param lapRepository the lap repository
     */
    @Autowired
    public SeriesController(SeriesService seriesService, LapRepository lapRepository) {
        this.seriesService = seriesService;
        this.lapRepository = lapRepository;
    }

    /**
     * Gets all series, including the years for each series.
     *
     * @return a response entity containing a list of all series with their years
     */
    @GetMapping
    public ResponseEntity<List<SeriesResponseDTO>> getAllSeries() {
        List<SeriesResponseDTO> series = seriesService.findAll();
        return ResponseEntity.ok(series);
    }

    @GetMapping("/{seriesId}/{year}/events")
    public ResponseEntity<List<EventsResponseDTO>> getEventsForSeriesByYear(@PathVariable Long seriesId, @PathVariable Integer year) {
        var years = seriesService.findEventsForSeriesByYear(seriesId, year);
        return ResponseEntity.ok(years);
    }

    /**
     * Gets all teams that participated in a specific event.
     *
     * @param eventId the ID of the event
     * @return a response entity containing the teams that participated in the event
     */
    @GetMapping("/events/{eventId}/teams")
    public ResponseEntity<TeamsResponseDTO> getTeamsByEventId(@PathVariable Long eventId) {
        TeamsResponseDTO teams = seriesService.findTeamsByEventId(eventId);
        return ResponseEntity.ok(teams);
    }

    /**
     * Gets all classes that competed in a specific event.
     *
     * @param eventId the ID of the event
     * @return a response entity containing the classes that competed in the event
     */
    @GetMapping("/events/{eventId}/classes")
    public ResponseEntity<ClassesResponseDTO> getClassesByEventId(@PathVariable Long eventId) {
        ClassesResponseDTO classes = seriesService.findClassesByEventId(eventId);
        return ResponseEntity.ok(classes);
    }

    /**
     * Gets all car models in a specific class for a specific event.
     *
     * @param eventId the ID of the event
     * @param classId the ID of the class
     * @return a response entity containing the car models in the specified class for the event
     */
    @GetMapping("/events/{eventId}/classes/{classId}/cars")
    public ResponseEntity<CarModelsResponseDTO> getCarModelsByEventIdAndClassId(@PathVariable Long eventId, @PathVariable Long classId) {
        CarModelsResponseDTO carModels = seriesService.findCarModelsByEventIdAndClassId(eventId, classId);
        return ResponseEntity.ok(carModels);
    }

    /**
     * Gets all sessions for a specific event.
     *
     * @param eventId the ID of the event
     * @return a response entity containing the sessions for the event
     */
    @GetMapping("/events/{eventId}/sessions")
    public ResponseEntity<SessionsResponseDTO> getSessionsByEventId(@PathVariable Long eventId) {
        SessionsResponseDTO sessions = seriesService.findSessionsByEventId(eventId);
        return ResponseEntity.ok(sessions);
    }

    /**
     * Gets lap time analysis for an event.
     *
     * @param eventId    the ID of the event
     * @param percentage the percentage of top lap times to include in the average calculation (default: 20)
     * @param classId    optional filter by class ID
     * @param carId      optional filter by car model ID
     * @param sessionId  optional filter by session ID
     * @param offset     optional pagination offset
     * @param limit      optional pagination limit
     * @return a response entity containing the lap time analysis for the event
     */
    @GetMapping("/events/{eventId}/laptimeanalysis")
    public ResponseEntity<LapTimeAnalysisResponseDTO> getLapTimeAnalysisForEvent(
            @PathVariable Long eventId,
            @RequestParam(defaultValue = "20") int percentage,
            @RequestParam(required = false) Long classId,
            @RequestParam(required = false) Long carId,
            @RequestParam(required = false) Long sessionId,
            @RequestParam(required = false) Integer offset,
            @RequestParam(required = false) Integer limit) {

        // Calculate overall lap time analysis
        LapTimeAnalysisDTO overallAnalysis = lapRepository.calculateLapTimeAnalysisForEvent(
                eventId,
                percentage,
                Optional.ofNullable(classId),
                Optional.ofNullable(carId),
                Optional.ofNullable(sessionId),
                Optional.ofNullable(offset),
                Optional.ofNullable(limit));

        // Calculate driver-specific lap time analyses
        List<DriverLapTimeAnalysisDTO> driverAnalyses = lapRepository.calculateLapTimeAnalysisPerDriverForEvent(
                eventId,
                percentage,
                Optional.ofNullable(classId),
                Optional.ofNullable(carId),
                Optional.ofNullable(sessionId),
                Optional.ofNullable(offset),
                Optional.ofNullable(limit));

        // Create the response DTO with both overall and driver-specific analyses
        LapTimeAnalysisResponseDTO response = new LapTimeAnalysisResponseDTO(eventId, driverAnalyses, overallAnalysis);

        return ResponseEntity.ok(response);
    }

    /**
     * Gets all drivers for a specific event with optional filters.
     *
     * @param eventId    the ID of the event
     * @param carModelId optional filter by car model ID
     * @param classId    optional filter by class ID
     * @return a response entity containing the drivers for the event
     */
    @GetMapping("/events/{eventId}/drivers")
    public ResponseEntity<DriversResponseDTO> getDriversByEventId(
            @PathVariable Long eventId,
            @RequestParam(required = false) Long carModelId,
            @RequestParam(required = false) Long classId) {

        DriversResponseDTO drivers = seriesService.findDriversByEventId(eventId, carModelId, classId);
        return ResponseEntity.ok(drivers);
    }

    /**
     * Gets lap times for multiple drivers in a specific session.
     *
     * @param eventId   the ID of the event
     * @param sessionId the ID of the session
     * @param driverIds comma-separated list of driver IDs
     * @return a response entity containing the lap times for the specified drivers
     */
    @GetMapping("/events/{eventId}/session/{sessionId}/laptimes")
    public ResponseEntity<LapTimesResponseDTO> getLapTimesForDriversInSession(
            @PathVariable Long eventId,
            @PathVariable Long sessionId,
            @RequestParam String driverIds) {

        // Parse comma-separated driver IDs
        List<Long> driverIdList = new ArrayList<>();
        if (driverIds != null && !driverIds.trim().isEmpty()) {
            String[] ids = driverIds.split(",");
            for (String id : ids) {
                try {
                    driverIdList.add(Long.parseLong(id.trim()));
                } catch (NumberFormatException e) {
                    return ResponseEntity.badRequest().build();
                }
            }
        }

        if (driverIdList.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        // Get lap times for the specified drivers
        List<DriverLapTimesDTO> driverLapTimes = lapRepository.findLapTimesForDriversInSession(sessionId, driverIdList);

        // Create response DTO
        LapTimesResponseDTO response = new LapTimesResponseDTO(eventId, sessionId, driverLapTimes);

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<SeriesResponseDTO> createSeries(
            @RequestBody SeriesDTO seriesDTO,
            @RequestHeader(value = "X-API-Key", required = false) String apiKey) {
        if (apiKey == null || !apiKey.equals(expectedApiKey)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        SeriesResponseDTO created = seriesService.createSeries(seriesDTO);
        return ResponseEntity.ok(created);
    }
}
