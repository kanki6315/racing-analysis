package com.arjunakankipati.racingstatanalysis.service.impl;

import com.arjunakankipati.racingstatanalysis.dto.*;
import com.arjunakankipati.racingstatanalysis.exceptions.ResourceNotFoundException;
import com.arjunakankipati.racingstatanalysis.model.*;
import com.arjunakankipati.racingstatanalysis.model.Class;
import com.arjunakankipati.racingstatanalysis.model.Session;
import com.arjunakankipati.racingstatanalysis.repository.*;
import com.arjunakankipati.racingstatanalysis.service.SeriesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of the SeriesService interface.
 */
@Service
public class SeriesServiceImpl implements SeriesService {

    private final SeriesRepository seriesRepository;
    private final EventRepository eventRepository;
    private final SessionRepository sessionRepository;
    private final CarRepository carRepository;
    private final TeamRepository teamRepository;
    private final CarDriverRepository carDriverRepository;
    private final DriverRepository driverRepository;
    private final ClassRepository classRepository;

    /**
     * Constructor with repository dependency injection.
     *
     * @param seriesRepository the series repository
     * @param eventRepository the event repository
     * @param sessionRepository the session repository
     * @param carRepository the car repository
     * @param teamRepository the team repository
     * @param carDriverRepository the car driver repository
     * @param driverRepository the driver repository
     * @param classRepository the class repository
     */
    @Autowired
    public SeriesServiceImpl(SeriesRepository seriesRepository,
                             EventRepository eventRepository,
                             SessionRepository sessionRepository,
                             CarRepository carRepository,
                             TeamRepository teamRepository,
                             CarDriverRepository carDriverRepository,
                             DriverRepository driverRepository,
                             ClassRepository classRepository) {
        this.seriesRepository = seriesRepository;
        this.eventRepository = eventRepository;
        this.sessionRepository = sessionRepository;
        this.carRepository = carRepository;
        this.teamRepository = teamRepository;
        this.carDriverRepository = carDriverRepository;
        this.driverRepository = driverRepository;
        this.classRepository = classRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<SeriesResponseDTO> findAll() {
        List<Series> seriesList = seriesRepository.findAll();

        return seriesList.stream()
                .map(series -> {
                    // Count events for this series
                    long eventCount = eventRepository.findBySeriesId(series.getId()).size();

                    // Create DTO with series data and event count
                    return new SeriesResponseDTO(
                            series.getId(),
                            series.getName(),
                            (int) eventCount
                    );
                })
                .toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public YearsResponseDTO findYearsBySeries(Long seriesId) {
        // Find the series by ID
        Series series = seriesRepository.findById(seriesId)
                .orElseThrow(ResourceNotFoundException::new);

        // Get years for this series
        List<Integer> years = eventRepository.findYearsBySeriesId(seriesId);

        // Create response DTO
        return new YearsResponseDTO(
                series.getId(),
                series.getName(),
                years
        );
    }

    @Override
    public List<EventsResponseDTO> findEventsForSeriesByYear(Long seriesId, Integer year) {
        // Find the series by ID
        Series series = seriesRepository.findById(seriesId)
                .orElseThrow(ResourceNotFoundException::new);

        var events = eventRepository.findBySeriesIdAndYear(seriesId, year);

        return events.stream().map(event -> new EventsResponseDTO(
                        event.getId(),
                        event.getSeriesId(),
                        event.getName(),
                        event.getYear(),
                        event.getStartDate(),
                        event.getEndDate(),
                        event.getDescription()))
                .toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TeamsResponseDTO findTeamsByEventId(Long eventId) {
        // Find the event by ID
        Event event = eventRepository.findById(eventId)
                .orElseThrow(ResourceNotFoundException::new);

        // Create response DTO
        TeamsResponseDTO response = new TeamsResponseDTO(event.getId(), event.getName());

        // Fetch all teams with their cars and drivers for the event in a single query
        Map<Long, TeamDTO> teamMap = teamRepository.findTeamsWithCarsAndDriversByEventId(eventId);

        // Add all teams to response
        teamMap.values().forEach(response::addTeam);

        return response;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ClassesResponseDTO findClassesByEventId(Long eventId) {
        // Find the event by ID
        Event event = eventRepository.findById(eventId)
                .orElseThrow(ResourceNotFoundException::new);

        // Find all classes that competed in this event
        List<Class> classes = classRepository.findByEventId(eventId);

        // Convert to DTOs
        List<ClassDTO> classDTOs = classes.stream()
                .map(clazz -> new ClassDTO(
                        clazz.getId(),
                        clazz.getSeriesId(),
                        clazz.getName(),
                        clazz.getDescription()
                ))
                .toList();

        // Create response DTO
        return new ClassesResponseDTO(event.getId(), event.getName(), classDTOs);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CarsResponseDTO findCarsByEventIdAndClassId(Long eventId, Long classId) {
        // Find the event by ID
        Event event = eventRepository.findById(eventId)
                .orElseThrow(ResourceNotFoundException::new);

        // Find the class by ID
        Class clazz = classRepository.findById(classId)
                .orElseThrow(ResourceNotFoundException::new);

        // Find all cars in this class for this event
        List<Car> cars = carRepository.findByEventIdAndClassId(eventId, classId);

        // Convert to DTOs
        List<CarDTO> carDTOs = cars.stream()
                .map(car -> new CarDTO(
                        car.getId(),
                        car.getNumber(),
                        car.getModel(),
                        car.getTireSupplier(),
                        car.getClassId(),
                        car.getManufacturerId()
                ))
                .toList();

        // Create response DTO
        return new CarsResponseDTO(event.getId(), event.getName(), clazz.getId(), clazz.getName(), carDTOs);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SessionsResponseDTO findSessionsByEventId(Long eventId) {
        // Find the event by ID
        Event event = eventRepository.findById(eventId)
                .orElseThrow(ResourceNotFoundException::new);

        // Find all sessions for this event
        List<Session> sessions = sessionRepository.findByEventId(eventId);

        // Convert to DTOs
        List<SessionDTO> sessionDTOs = sessions.stream()
                .map(session -> new SessionDTO(
                        session.getId(),
                        session.getEventId(),
                        session.getCircuitId(),
                        session.getName(),
                        session.getType(),
                        session.getStartDatetime(),
                        session.getDurationSeconds(),
                        session.getWeatherAirTemp(),
                        session.getWeatherTrackTemp(),
                        session.getWeatherCondition(),
                        session.getReportMessage(),
                        session.getImportUrl(),
                        session.getImportTimestamp()
                ))
                .toList();

        // Create response DTO
        return new SessionsResponseDTO(event.getId(), event.getName(), sessionDTOs);
    }
}
