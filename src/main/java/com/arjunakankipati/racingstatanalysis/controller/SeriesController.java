package com.arjunakankipati.racingstatanalysis.controller;

import com.arjunakankipati.racingstatanalysis.dto.*;
import com.arjunakankipati.racingstatanalysis.repository.LapRepository;
import com.arjunakankipati.racingstatanalysis.service.SeriesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
     * Gets all series.
     *
     * @return a response entity containing a list of all series
     */
    @GetMapping
    public ResponseEntity<List<SeriesResponseDTO>> getAllSeries() {
        List<SeriesResponseDTO> series = seriesService.findAll();
        return ResponseEntity.ok(series);
    }

    /**
     * Gets all years for a specific series.
     *
     * @param seriesId the ID of the series
     * @return a response entity containing the years for the series
     */
    @GetMapping("/{seriesId}/years")
    public ResponseEntity<YearsResponseDTO> getYearsForSeries(@PathVariable Long seriesId) {
        YearsResponseDTO years = seriesService.findYearsBySeries(seriesId);
        return ResponseEntity.ok(years);
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
     * Gets all cars in a specific class for a specific event.
     *
     * @param eventId the ID of the event
     * @param classId the ID of the class
     * @return a response entity containing the cars in the specified class for the event
     */
    @GetMapping("/events/{eventId}/classes/{classId}/cars")
    public ResponseEntity<CarsResponseDTO> getCarsByEventIdAndClassId(@PathVariable Long eventId, @PathVariable Long classId) {
        CarsResponseDTO cars = seriesService.findCarsByEventIdAndClassId(eventId, classId);
        return ResponseEntity.ok(cars);
    }

    /**
     * Gets lap time analysis for an event.
     *
     * @param eventId    the ID of the event
     * @param percentage the percentage of top lap times to include in the average calculation (default: 20)
     * @param classId    optional filter by class ID
     * @param carId      optional filter by car ID
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
}
