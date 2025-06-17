package com.arjunakankipati.racingstatanalysis.service.impl;

import com.arjunakankipati.racingstatanalysis.dto.LapTimeDTO;
import com.arjunakankipati.racingstatanalysis.exceptions.ResourceNotFoundException;
import com.arjunakankipati.racingstatanalysis.model.*;
import com.arjunakankipati.racingstatanalysis.repository.*;
import com.arjunakankipati.racingstatanalysis.service.DriverAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of the DriverAnalysisService interface.
 */
@Service
public class DriverAnalysisServiceImpl implements DriverAnalysisService {

    private final LapRepository lapRepository;
    private final DriverRepository driverRepository;
    private final CarRepository carRepository;
    private final TeamRepository teamRepository;
    private final EventRepository eventRepository;
    private final SessionRepository sessionRepository;

    /**
     * Constructor with repository dependency injection.
     *
     * @param lapRepository     the lap repository
     * @param driverRepository  the driver repository
     * @param carRepository     the car repository
     * @param teamRepository    the team repository
     * @param eventRepository   the event repository
     * @param sessionRepository the session repository
     */
    @Autowired
    public DriverAnalysisServiceImpl(LapRepository lapRepository,
                                     DriverRepository driverRepository,
                                     CarRepository carRepository,
                                     TeamRepository teamRepository,
                                     EventRepository eventRepository,
                                     SessionRepository sessionRepository) {
        this.lapRepository = lapRepository;
        this.driverRepository = driverRepository;
        this.carRepository = carRepository;
        this.teamRepository = teamRepository;
        this.eventRepository = eventRepository;
        this.sessionRepository = sessionRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<LapTimeDTO> getTopLapTimes(Long driverId, int percentage, 
                                         Optional<Long> seriesId, 
                                         Optional<Integer> year, 
                                         Optional<Long> eventId,
                                         Optional<Long> sessionId) {
        // Verify driver exists
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(ResourceNotFoundException::new);

        // Get filtered laps for the driver using SQL query
        List<Lap> filteredLaps = lapRepository.findFilteredLaps(driverId, true, sessionId, eventId, year, seriesId);

        // If no laps found after filtering, return empty list
        if (filteredLaps.isEmpty()) {
            return new ArrayList<>();
        }

        // Sort laps by lap time (ascending)
        filteredLaps.sort(Comparator.comparing(Lap::getLapTimeSeconds));

        // Calculate how many laps to return based on percentage
        int topCount = Math.max(1, (int) Math.ceil(filteredLaps.size() * percentage / 100.0));

        // Take the top N laps
        List<Lap> topLaps = filteredLaps.stream()
                .limit(topCount)
                .collect(Collectors.toList());

        // Map laps to DTOs
        return mapLapsToLapTimeDTOs(topLaps, driver);
    }


    /**
     * Maps Lap entities to LapTimeDTO objects.
     *
     * @param laps   the laps to map
     * @param driver the driver for the laps
     * @return the mapped DTOs
     */
    private List<LapTimeDTO> mapLapsToLapTimeDTOs(List<Lap> laps, Driver driver) {
        // Create maps to cache entities and avoid repeated database queries
        Map<Long, Car> carCache = new HashMap<>();
        Map<Long, Team> teamCache = new HashMap<>();
        Map<Long, Session> sessionCache = new HashMap<>();
        Map<Long, Event> eventCache = new HashMap<>();

        return laps.stream().map(lap -> {
            // Get car
            Car car = carCache.computeIfAbsent(lap.getCarId(),
                    id -> carRepository.findById(id).orElse(null));

            if (car == null) {
                return null;
            }

            // Get team
            Team team = teamCache.computeIfAbsent(car.getTeamId(),
                    id -> teamRepository.findById(id).orElse(null));

            // Get session
            Session session = sessionCache.computeIfAbsent(car.getSessionId(),
                    id -> sessionRepository.findById(id).orElse(null));

            if (session == null) {
                return null;
            }

            // Get event
            Event event = eventCache.computeIfAbsent(session.getEventId(),
                    id -> eventRepository.findById(id).orElse(null));

            if (event == null) {
                return null;
            }

            // Format lap time as "m:ss.SSS"
            String lapTime = formatLapTime(lap.getLapTimeSeconds());

            // Create DTO
            return new LapTimeDTO(
                    lapTime,
                    lap.getAverageSpeedKph(),
                    event.getName(),
                    event.getYear(),
                    car.getNumber(),
                    team != null ? team.getName() : "Unknown Team",
                    driver.getFullName(),
                    lap.getTimestamp()
            );
        }).filter(dto -> dto != null).collect(Collectors.toList());
    }

    /**
     * Formats a lap time in seconds as "m:ss.SSS".
     *
     * @param lapTimeSeconds the lap time in seconds
     * @return the formatted lap time
     */
    private String formatLapTime(BigDecimal lapTimeSeconds) {
        if (lapTimeSeconds == null) {
            return "0:00.000";
        }

        // Extract minutes, seconds, and milliseconds
        int totalSeconds = lapTimeSeconds.intValue();
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;

        // Extract milliseconds (the decimal part)
        BigDecimal decimalPart = lapTimeSeconds.remainder(BigDecimal.ONE);
        int milliseconds = decimalPart.movePointRight(3).intValue();

        // Format as "m:ss.SSS"
        return String.format("%d:%02d.%03d", minutes, seconds, milliseconds);
    }
}
