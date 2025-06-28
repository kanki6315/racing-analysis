package com.arjunakankipati.racingstatanalysis.service.impl;

import com.arjunakankipati.racingstatanalysis.dto.*;
import com.arjunakankipati.racingstatanalysis.exceptions.ResourceNotFoundException;
import com.arjunakankipati.racingstatanalysis.model.*;
import com.arjunakankipati.racingstatanalysis.model.Class;
import com.arjunakankipati.racingstatanalysis.repository.*;
import com.arjunakankipati.racingstatanalysis.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class EventServiceImpl implements EventService {


    @Autowired
    private SeriesRepository seriesRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private SessionRepository sessionRepository;
    @Autowired
    private CarEntryRepository carEntryRepository;
    @Autowired
    private CarModelRepository carModelRepository;
    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private CarDriverRepository carDriverRepository;
    @Autowired
    private DriverRepository driverRepository;
    @Autowired
    private ClassRepository classRepository;

    @Override
    public EventDTO createEvent(EventDTO eventDTO) {
        Event event = new Event();
        event.setSeriesId(eventDTO.getSeriesId());
        event.setCircuitId(event.getCircuitId());
        event.setName(eventDTO.getName());
        event.setYear(eventDTO.getStartDate().getYear());
        event.setStartDate(eventDTO.getStartDate());
        event.setEndDate(eventDTO.getEndDate());
        event.setDescription(eventDTO.getDescription());
        Event saved = eventRepository.save(event);
        return new EventDTO(
                saved.getId(),
                saved.getSeriesId(),
                saved.getName(),
                saved.getYear(),
                saved.getStartDate(),
                saved.getEndDate(),
                saved.getDescription()
        );
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public List<EventsResponseDTO> findEventsForSeriesByYear(Long seriesId, Integer year) {
        // Find the series by ID
        Series series = seriesRepository.findById(seriesId)
                .orElseThrow(ResourceNotFoundException::new);

        var events = eventRepository.findBySeriesIdAndYear(seriesId, year);

        return events.stream().map(event -> new EventsResponseDTO(
                        event.getId(),
                        event.getSeriesId(),
                        event.getCircuitId(),
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

        // Find all car entries in this class for this event
        List<CarEntry> carEntries = carEntryRepository.findByEventIdAndClassId(eventId, classId);

        // Convert to DTOs with car model information
        List<CarEntryDTO> carEntryDTOS = carEntries.stream()
                .map(carEntry -> {
                    // Get the car model information
                    CarModel carModel = carModelRepository.findById(carEntry.getCarModelId())
                            .orElse(null);

                    CarModelDTO carModelDTO = null;
                    if (carModel != null) {
                        carModelDTO = new CarModelDTO(
                                carModel.getId(),
                                carModel.getName(),
                                carModel.getFullName(),
                                carModel.getYearModel(),
                                carModel.getDescription()
                        );
                    }

                    return new CarEntryDTO(
                            carEntry.getId(),
                            carEntry.getNumber(),
                            carModelDTO,
                            carEntry.getTireSupplier(),
                            carEntry.getClassId(),
                            carEntry.getTeamId(),
                            null
                    );
                })
                .toList();

        // Create response DTO
        return new CarsResponseDTO(event.getId(), event.getName(), clazz.getId(), clazz.getName(), carEntryDTOS);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CarModelsResponseDTO findCarModelsByEventIdAndClassId(Long eventId, Long classId) {
        // Find the event by ID
        Event event = eventRepository.findById(eventId)
                .orElseThrow(ResourceNotFoundException::new);

        // Find the class by ID
        Class clazz = classRepository.findById(classId)
                .orElseThrow(ResourceNotFoundException::new);

        // Find all car entries in this class for this event to get unique car models
        List<CarEntry> carEntries = carEntryRepository.findByEventIdAndClassId(eventId, classId);

        // Extract unique car models from car entries
        List<CarModelDTO> carModelDTOs = carEntries.stream()
                .map(carEntry -> {
                    // Get the car model information
                    CarModel carModel = carModelRepository.findById(carEntry.getCarModelId())
                            .orElse(null);

                    if (carModel != null) {
                        CarModelDTO carModelDTO = new CarModelDTO(
                                carModel.getId(),
                                carModel.getName(),
                                carModel.getFullName(),
                                carModel.getYearModel(),
                                carModel.getDescription()
                        );
                        return carModelDTO;
                    }
                    return null;
                })
                .filter(carModelDTO -> carModelDTO != null)
                .distinct() // Remove duplicates based on car model ID
                .toList();

        // Create response DTO
        return new CarModelsResponseDTO(event.getId(), event.getName(), clazz.getId(), clazz.getName(), carModelDTOs);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DriversResponseDTO findDriversByEventId(Long eventId, Long carModelId, Long classId) {
        // Find the event by ID
        Event event = eventRepository.findById(eventId)
                .orElseThrow(ResourceNotFoundException::new);

        // Get all car entries for the event
        List<CarEntry> carEntries;
        if (classId != null) {
            // Filter by class if provided
            carEntries = carEntryRepository.findByEventIdAndClassId(eventId, classId);
        } else {
            // Get all car entries for the event by getting all sessions and their car entries
            List<Session> sessions = sessionRepository.findByEventId(eventId);
            carEntries = sessions.stream()
                    .flatMap(session -> carEntryRepository.findBySessionId(session.getId()).stream())
                    .distinct()
                    .toList();
        }

        // Filter by car model if provided
        if (carModelId != null) {
            carEntries = carEntries.stream()
                    .filter(carEntry -> carModelId.equals(carEntry.getCarModelId()))
                    .toList();
        }

        // Get all drivers for these car entries with team and car information
        List<DriverWithTeamDTO> driverWithTeamDTOs = carEntries.stream()
                .flatMap(carEntry -> {
                    // Get car drivers for this car entry
                    List<CarDriver> carDrivers = carDriverRepository.findByCarId(carEntry.getId());

                    return carDrivers.stream()
                            .map(carDriver -> {
                                // Get driver information
                                Driver driver = driverRepository.findById(carDriver.getDriverId()).orElse(null);
                                if (driver != null) {
                                    // Get team information
                                    Team team = teamRepository.findById(carEntry.getTeamId()).orElse(null);
                                    String teamName = team != null ? team.getName() : null;

                                    // Get car model information
                                    CarModel carModel = carModelRepository.findById(carEntry.getCarModelId()).orElse(null);
                                    CarModelDTO carModelDTO = null;
                                    if (carModel != null) {
                                        carModelDTO = new CarModelDTO(
                                                carModel.getId(),
                                                carModel.getName(),
                                                carModel.getFullName(),
                                                carModel.getYearModel(),
                                                carModel.getDescription()
                                        );
                                    }

                                    return new DriverWithTeamDTO(
                                            driver.getId(),
                                            driver.getFirstName(),
                                            driver.getLastName(),
                                            driver.getNationality(),
                                            driver.getHometown(),
                                            driver.getLicenseType(),
                                            carDriver.getDriverNumber(),
                                            carEntry.getTeamId(),
                                            teamName,
                                            carEntry.getNumber(),
                                            carModelDTO
                                    );
                                }
                                return null;
                            })
                            .filter(driverWithTeamDTO -> driverWithTeamDTO != null);
                })
                .distinct() // Remove duplicate drivers
                .toList();

        // Create response DTO
        return new DriversResponseDTO(event.getId(), event.getName(), driverWithTeamDTOs);
    }
} 