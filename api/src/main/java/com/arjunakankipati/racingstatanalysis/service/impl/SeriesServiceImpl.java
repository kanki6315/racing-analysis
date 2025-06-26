package com.arjunakankipati.racingstatanalysis.service.impl;

import com.arjunakankipati.racingstatanalysis.dto.*;
import com.arjunakankipati.racingstatanalysis.exceptions.ResourceNotFoundException;
import com.arjunakankipati.racingstatanalysis.model.*;
import com.arjunakankipati.racingstatanalysis.model.Class;
import com.arjunakankipati.racingstatanalysis.repository.*;
import com.arjunakankipati.racingstatanalysis.service.SeriesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Implementation of the SeriesService interface.
 */
@Service
public class SeriesServiceImpl implements SeriesService {

    private final SeriesRepository seriesRepository;
    private final EventRepository eventRepository;
    private final SessionRepository sessionRepository;
    private final CarEntryRepository carEntryRepository;
    private final CarModelRepository carModelRepository;
    private final TeamRepository teamRepository;
    private final CarDriverRepository carDriverRepository;
    private final DriverRepository driverRepository;
    private final ClassRepository classRepository;
    private final ManufacturerRepository manufacturerRepository;

    /**
     * Constructor with repository dependency injection.
     *
     * @param seriesRepository the series repository
     * @param eventRepository the event repository
     * @param sessionRepository the session repository
     * @param carEntryRepository the car entry repository
     * @param carModelRepository the car model repository
     * @param teamRepository the team repository
     * @param carDriverRepository the car driver repository
     * @param driverRepository the driver repository
     * @param classRepository the class repository
     * @param manufacturerRepository the manufacturer repository
     */
    @Autowired
    public SeriesServiceImpl(SeriesRepository seriesRepository,
                             EventRepository eventRepository,
                             SessionRepository sessionRepository,
                             CarEntryRepository carEntryRepository,
                             CarModelRepository carModelRepository,
                             TeamRepository teamRepository,
                             CarDriverRepository carDriverRepository,
                             DriverRepository driverRepository,
                             ClassRepository classRepository,
                             ManufacturerRepository manufacturerRepository) {
        this.seriesRepository = seriesRepository;
        this.eventRepository = eventRepository;
        this.sessionRepository = sessionRepository;
        this.carEntryRepository = carEntryRepository;
        this.carModelRepository = carModelRepository;
        this.teamRepository = teamRepository;
        this.carDriverRepository = carDriverRepository;
        this.driverRepository = driverRepository;
        this.classRepository = classRepository;
        this.manufacturerRepository = manufacturerRepository;
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
                    // TODO - make one query and return event count and years
                    var events = eventRepository.findBySeriesId(series.getId());
                    // Create DTO with series data, event count, and years
                    return new SeriesResponseDTO(
                            series.getId(),
                            series.getName(),
                            events.size(),
                            events.stream()
                                    .map(Event::getYear)
                                    .distinct()
                                    .toList()
                    );
                })
                .toList();
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
        List<CarDTO> carDTOs = carEntries.stream()
                .map(carEntry -> {
                    // Get the car model information
                    CarModel carModel = carModelRepository.findById(carEntry.getCarModelId())
                            .orElse(null);

                    CarModelDTO carModelDTO = null;
                    if (carModel != null) {
                        // Get manufacturer information
                        Manufacturer manufacturer = null;
                        ManufacturerDTO manufacturerDTO = null;
                        if (carModel.getManufacturerId() != null) {
                            manufacturer = manufacturerRepository.findById(carModel.getManufacturerId()).orElse(null);
                            if (manufacturer != null) {
                                manufacturerDTO = new ManufacturerDTO(
                                        manufacturer.getId(),
                                        manufacturer.getName(),
                                        manufacturer.getCountry(),
                                        null // Manufacturer model doesn't have description field
                                );
                            }
                        }

                        carModelDTO = new CarModelDTO(
                                carModel.getId(),
                                carModel.getManufacturerId(),
                                carModel.getName(),
                                carModel.getFullName(),
                                carModel.getYearModel(),
                                carModel.getDescription()
                        );
                        carModelDTO.setManufacturer(manufacturerDTO);
                    }

                    return new CarDTO(
                            carEntry.getId(),
                            carEntry.getNumber(),
                            carModelDTO,
                            carEntry.getTireSupplier(),
                            carEntry.getClassId(),
                            carEntry.getTeamId()
                    );
                })
                .toList();

        // Create response DTO
        return new CarsResponseDTO(event.getId(), event.getName(), clazz.getId(), clazz.getName(), carDTOs);
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
                        // Get manufacturer information
                        Manufacturer manufacturer = null;
                        ManufacturerDTO manufacturerDTO = null;
                        if (carModel.getManufacturerId() != null) {
                            manufacturer = manufacturerRepository.findById(carModel.getManufacturerId()).orElse(null);
                            if (manufacturer != null) {
                                manufacturerDTO = new ManufacturerDTO(
                                        manufacturer.getId(),
                                        manufacturer.getName(),
                                        manufacturer.getCountry(),
                                        null // Manufacturer model doesn't have description field
                                );
                            }
                        }

                        CarModelDTO carModelDTO = new CarModelDTO(
                                carModel.getId(),
                                carModel.getManufacturerId(),
                                carModel.getName(),
                                carModel.getFullName(),
                                carModel.getYearModel(),
                                carModel.getDescription()
                        );
                        carModelDTO.setManufacturer(manufacturerDTO);
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
                                        // Get manufacturer information
                                        Manufacturer manufacturer = null;
                                        ManufacturerDTO manufacturerDTO = null;
                                        if (carModel.getManufacturerId() != null) {
                                            manufacturer = manufacturerRepository.findById(carModel.getManufacturerId()).orElse(null);
                                            if (manufacturer != null) {
                                                manufacturerDTO = new ManufacturerDTO(
                                                        manufacturer.getId(),
                                                        manufacturer.getName(),
                                                        manufacturer.getCountry(),
                                                        null // Manufacturer model doesn't have description field
                                                );
                                            }
                                        }

                                        carModelDTO = new CarModelDTO(
                                                carModel.getId(),
                                                carModel.getManufacturerId(),
                                                carModel.getName(),
                                                carModel.getFullName(),
                                                carModel.getYearModel(),
                                                carModel.getDescription()
                                        );
                                        carModelDTO.setManufacturer(manufacturerDTO);
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

    /**
     * {@inheritDoc}
     */
    @Override
    public SeriesResponseDTO createSeries(SeriesDTO seriesDTO) {
        Series series = new Series();
        series.setName(seriesDTO.getName());
        series.setDescription(seriesDTO.getDescription());
        Series saved = seriesRepository.save(series);
        return new SeriesResponseDTO(saved.getId(), saved.getName(), 0, null);
    }
}
