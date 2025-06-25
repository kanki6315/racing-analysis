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
    private final CarEntryRepository carEntryRepository;
    private final DriverRepository driverRepository;
    private final CarDriverRepository carDriverRepository;
    private final TeamRepository teamRepository;
    private final ClassRepository classRepository;
    private final SessionRepository sessionRepository;
    private final EventRepository eventRepository;

    Map<Long, CarEntry> carCache = new HashMap<>();
    Map<Long, Team> teamCache = new HashMap<>();
    Map<Long, Session> sessionCache = new HashMap<>();
    Map<Long, Event> eventCache = new HashMap<>();

    /**
     * Constructor with repository dependency injection.
     *
     * @param lapRepository the lap repository
     * @param carEntryRepository the car entry repository
     * @param driverRepository the driver repository
     * @param carDriverRepository the car driver repository
     * @param teamRepository the team repository
     * @param classRepository the class repository
     * @param sessionRepository the session repository
     * @param eventRepository the event repository
     */
    @Autowired
    public DriverAnalysisServiceImpl(LapRepository lapRepository,
                                     CarEntryRepository carEntryRepository,
                                     DriverRepository driverRepository,
                                     CarDriverRepository carDriverRepository,
                                     TeamRepository teamRepository,
                                     ClassRepository classRepository,
                                     SessionRepository sessionRepository,
                                     EventRepository eventRepository) {
        this.lapRepository = lapRepository;
        this.carEntryRepository = carEntryRepository;
        this.driverRepository = driverRepository;
        this.carDriverRepository = carDriverRepository;
        this.teamRepository = teamRepository;
        this.classRepository = classRepository;
        this.sessionRepository = sessionRepository;
        this.eventRepository = eventRepository;
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

        // Get top percentage of laps for the driver using SQL query
        // This handles filtering, sorting, and percentage-based limiting in the database
        List<Lap> topLaps = lapRepository.findTopPercentageLapsByDriverId(
                driverId, true, percentage, sessionId, eventId, year, seriesId);

        // If no laps found, return empty list
        if (topLaps.isEmpty()) {
            return new ArrayList<>();
        }

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
        return laps.stream().map(lap -> {
            // Get car entry
            CarEntry carEntry = carCache.computeIfAbsent(lap.getCarId(),
                    id -> carEntryRepository.findById(id).orElse(null));

            if (carEntry == null) {
                return null;
            }

            // Get team
            Team team = teamCache.computeIfAbsent(carEntry.getTeamId(),
                    id -> teamRepository.findById(id).orElse(null));

            // Get session
            Session session = sessionCache.computeIfAbsent(carEntry.getSessionId(),
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
                    carEntry.getNumber(),
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
