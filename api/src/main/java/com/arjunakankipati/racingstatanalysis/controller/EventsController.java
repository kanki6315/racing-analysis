package com.arjunakankipati.racingstatanalysis.controller;

import com.arjunakankipati.racingstatanalysis.dto.*;
import com.arjunakankipati.racingstatanalysis.repository.LapRepository;
import com.arjunakankipati.racingstatanalysis.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/events")
public class EventsController {
    @Value("${api.key}")
    private String expectedApiKey;

    @Autowired
    private EventService eventService;
    @Autowired
    private LapRepository lapRepository;

    @PostMapping
    public ResponseEntity<EventDTO> createEvent(
            @RequestBody EventDTO eventDTO,
            @RequestHeader(value = "X-API-Key", required = false) String apiKey) {
        if (apiKey == null || !apiKey.equals(expectedApiKey)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        EventDTO response = eventService.createEvent(eventDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<EventsResponseDTO>> getEventsForSeriesByYear(
            @RequestParam Long seriesId, @RequestParam Integer year) {
        var years = eventService.findEventsForSeriesByYear(seriesId, year);
        return ResponseEntity.ok(years);
    }

    /**
     * Gets all teams that participated in a specific event.
     *
     * @param eventId the ID of the event
     * @return a response entity containing the teams that participated in the event
     */
    @GetMapping("/{eventId}/teams")
    public ResponseEntity<TeamsResponseDTO> getTeamsByEventId(@PathVariable Long eventId) {
        TeamsResponseDTO teams = eventService.findTeamsByEventId(eventId);
        return ResponseEntity.ok(teams);
    }

    /**
     * Gets all classes that competed in a specific event.
     *
     * @param eventId the ID of the event
     * @return a response entity containing the classes that competed in the event
     */
    @GetMapping("/{eventId}/classes")
    public ResponseEntity<ClassesResponseDTO> getClassesByEventId(@PathVariable Long eventId) {
        ClassesResponseDTO classes = eventService.findClassesByEventId(eventId);
        return ResponseEntity.ok(classes);
    }

    /**
     * Gets all car models in a specific class for a specific event.
     *
     * @param eventId the ID of the event
     * @param classId the ID of the class
     * @return a response entity containing the car models in the specified class for the event
     */
    @GetMapping("/{eventId}/classes/{classId}/cars")
    public ResponseEntity<CarModelsResponseDTO> getCarModelsByEventIdAndClassId(@PathVariable Long eventId, @PathVariable Long classId) {
        CarModelsResponseDTO carModels = eventService.findCarModelsByEventIdAndClassId(eventId, classId);
        return ResponseEntity.ok(carModels);
    }

    /**
     * Gets all drivers for a specific event with optional filters.
     *
     * @param eventId    the ID of the event
     * @param carModelId optional filter by car model ID
     * @param classId    optional filter by class ID
     * @return a response entity containing the drivers for the event
     */
    @GetMapping("/{eventId}/drivers")
    public ResponseEntity<DriversResponseDTO> getDriversByEventId(
            @PathVariable Long eventId,
            @RequestParam(required = false) Long carModelId,
            @RequestParam(required = false) Long classId) {

        DriversResponseDTO drivers = eventService.findDriversByEventId(eventId, carModelId, classId);
        return ResponseEntity.ok(drivers);
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
    @GetMapping("/{eventId}/laptimeanalysis")
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
     * Gets lap times for multiple drivers in a specific session.
     *
     * @param eventId   the ID of the event
     * @param sessionId the ID of the session
     * @param driverIds comma-separated list of driver IDs
     * @return a response entity containing the lap times for the specified drivers
     */
    @GetMapping("/{eventId}/session/{sessionId}/laptimes")
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
} 