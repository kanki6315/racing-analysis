package com.arjunakankipati.racingstatanalysis.service.impl;

import com.arjunakankipati.racingstatanalysis.dto.ImportRequestDTO;
import com.arjunakankipati.racingstatanalysis.dto.ProcessResultsRequestDTO;
import com.arjunakankipati.racingstatanalysis.dto.ProcessResultsResponseDTO;
import com.arjunakankipati.racingstatanalysis.exceptions.ReportURLNotValidException;
import com.arjunakankipati.racingstatanalysis.model.*;
import com.arjunakankipati.racingstatanalysis.model.Class;
import com.arjunakankipati.racingstatanalysis.repository.*;
import com.arjunakankipati.racingstatanalysis.service.ImportJobService;
import com.arjunakankipati.racingstatanalysis.service.ImportService;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.*;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of the ImportService interface.
 */
@Service
public class ImportServiceImpl implements ImportService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImportServiceImpl.class);
    private final SeriesRepository seriesRepository;
    private final EventRepository eventRepository;
    private final CircuitRepository circuitRepository;
    private final SessionRepository sessionRepository;
    private final TeamRepository teamRepository;
    private final ClassRepository classRepository;
    private final DriverRepository driverRepository;
    private final CarEntryRepository carEntryRepository;
    private final CarModelRepository carModelRepository;
    private final CarDriverRepository carDriverRepository;
    private final LapRepository lapRepository;
    private final SectorRepository sectorRepository;
    private final ImportJobService importJobService;

    private final OkHttpClient httpClient;

    private final Cache<String, Series> seriesCache = CacheBuilder.newBuilder().expireAfterWrite(20, TimeUnit.MINUTES).build();
    private final Cache<String, Circuit> circuitCache = CacheBuilder.newBuilder().expireAfterWrite(20, TimeUnit.MINUTES).build();
    private final Cache<String, Team> teamCache = CacheBuilder.newBuilder().expireAfterWrite(20, TimeUnit.MINUTES).build();
    private final Cache<String, Class> classCache = CacheBuilder.newBuilder().expireAfterWrite(20, TimeUnit.MINUTES).build();
    private final Cache<String, CarModel> carModelCache = CacheBuilder.newBuilder().expireAfterWrite(20, TimeUnit.MINUTES).build();
    private final Cache<String, Event> eventCache = CacheBuilder.newBuilder().expireAfterWrite(20, TimeUnit.MINUTES).build();
    private final Cache<String, Session> sessionCache = CacheBuilder.newBuilder().expireAfterWrite(20, TimeUnit.MINUTES).build();

    /**
     * Constructor with repository dependency injection.
     */
    @Autowired
    public ImportServiceImpl(SeriesRepository seriesRepository,
                             EventRepository eventRepository,
                             CircuitRepository circuitRepository,
                             SessionRepository sessionRepository,
                             TeamRepository teamRepository,
                             ClassRepository classRepository,
                             DriverRepository driverRepository,
                             CarEntryRepository carEntryRepository,
                             CarModelRepository carModelRepository,
                             CarDriverRepository carDriverRepository,
                             LapRepository lapRepository,
                             SectorRepository sectorRepository,
                             ImportJobService importJobService) {
        this.seriesRepository = seriesRepository;
        this.eventRepository = eventRepository;
        this.circuitRepository = circuitRepository;
        this.sessionRepository = sessionRepository;
        this.teamRepository = teamRepository;
        this.classRepository = classRepository;
        this.driverRepository = driverRepository;
        this.carEntryRepository = carEntryRepository;
        this.carModelRepository = carModelRepository;
        this.carDriverRepository = carDriverRepository;
        this.lapRepository = lapRepository;
        this.sectorRepository = sectorRepository;
        this.importJobService = importJobService;

        // Initialize OkHttpClient with reasonable timeouts
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();
    }

    @Async
    @Override
    public void processImport(Integer jobId, ImportRequestDTO request) {
        importJobService.markStarted(jobId);
        try {
            // Fetch JSON data from URL
            JsonObject jsonData = fetchJsonData(request.getUrl());
            // Process and store the data
            processJSONData(jsonData, request.getUrl());
            importJobService.markCompleted(jobId);
        } catch (Exception e) {
            LOGGER.error("Failed to process import for URL: " + request.getUrl(), e);
            importJobService.markFailed(jobId, e.getMessage());
        }
    }

    /**
     * Fetches JSON data from a URL.
     * For development purposes, it reads from the temp.json file if the URL is "temp".
     *
     * @param url the URL to fetch data from
     * @return the JSON data as a JsonObject
     * @throws IOException                if an I/O error occurs
     * @throws ReportURLNotValidException if the URL is not valid or the data is not valid JSON
     */
    private JsonObject fetchJsonData(String url) throws IOException {
        // For development purposes, read from temp.json if URL is "temp"
        if ("temp".equals(url)) {
            try (FileReader reader = new FileReader(new File("src/main/resources/temp.json"))) {
                return JsonParser.parseReader(reader).getAsJsonObject();
            } catch (JsonParseException e) {
                throw new ReportURLNotValidException("Invalid JSON format in temp.json", e);
            }
        }
        // Fetch data from URL
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new ReportURLNotValidException("Failed to fetch data from URL: " + response.code());
            }
            String jsonString = response.body().string();
            try {
                return JsonParser.parseString(jsonString).getAsJsonObject();
            } catch (JsonParseException e) {
                LOGGER.error("Failed to parse JSON data from URL: " + url, e);
                throw new ReportURLNotValidException("Invalid JSON format in response", e);
            }
        }
    }

    /**
     * Processes and stores the data from the JSON.
     *
     * @param jsonData the JSON data to process
     * @param url      the URL of the imported data
     */
    private void processJSONData(JsonObject jsonData, String url) {
        // Extract session data
        JsonObject sessionJson = jsonData.getAsJsonObject("session");
        JsonObject circuitJson = sessionJson.getAsJsonObject("circuit");

        // Extract series name, event name, and year from the JSON data
        String seriesName = sessionJson.get("championship_name").getAsString();
        String eventName = sessionJson.get("event_name").getAsString();

        // Extract year from session_date (format: "25/01/2025 01:40")
        String sessionDateStr = sessionJson.get("session_date").getAsString();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        LocalDateTime sessionStartDateTime = LocalDateTime.parse(sessionDateStr, formatter);
        int year = sessionStartDateTime.getYear();
        LocalDate eventStartDate = sessionStartDateTime.toLocalDate();
        int durationSeconds = 0;
        if (sessionJson.has("finalize_type") && sessionJson.get("finalize_type").isJsonObject()) {
            JsonObject finalizeType = sessionJson.getAsJsonObject("finalize_type");
            if (finalizeType.has("time_in_seconds")) {
                durationSeconds = finalizeType.get("time_in_seconds").getAsInt();
            }
        }
        LocalDate eventEndDate = eventStartDate;
        if (durationSeconds > 0) {
            LocalDateTime sessionEndDateTime = sessionStartDateTime.plusSeconds(durationSeconds);
            eventEndDate = sessionEndDateTime.toLocalDate();
        }
        // Find or create series
        Series series = findOrCreateSeries(seriesName);
        LOGGER.info(series.toString());

        // Find or create circuit
        Circuit circuit = findOrCreateCircuit(circuitJson);
        LOGGER.info(circuit.toString());

        // Find or create event
        Event event = findOrCreateEvent(eventName, year, series.getId(), eventStartDate, eventEndDate);
        LOGGER.info(event.toString());

        // Create session
        Session session = createSession(sessionJson, event.getId(), circuit.getId());
        LOGGER.info(session.toString());

        // Process participants
        JsonArray participantsJson = jsonData.getAsJsonArray("participants");
        processParticipants(participantsJson, series.getId(), session.getId());
    }

    /**
     * Finds or creates a series with the given name.
     *
     * @param seriesName the name of the series
     * @return the series entity
     */
    private Series findOrCreateSeries(String seriesName) {
        Series cached = seriesCache.getIfPresent(seriesName);
        if (cached != null) return cached;
        Optional<Series> existingSeries = seriesRepository.findByName(seriesName);
        if (existingSeries.isPresent()) {
            seriesCache.put(seriesName, existingSeries.get());
            return existingSeries.get();
        }
        Series newSeries = new Series();
        newSeries.setName(seriesName);
        Series saved = seriesRepository.save(newSeries);
        seriesCache.put(seriesName, saved);
        return saved;
    }

    /**
     * Finds or creates a circuit with the given data.
     *
     * @param circuitJson the JSON data for the circuit
     * @return the circuit entity
     */
    private Circuit findOrCreateCircuit(JsonObject circuitJson) {
        String name = circuitJson.get("name").getAsString();
        Circuit cached = circuitCache.getIfPresent(name);
        if (cached != null) return cached;
        Optional<Circuit> existingCircuit = circuitRepository.findByName(name);
        if (existingCircuit.isPresent()) {
            circuitCache.put(name, existingCircuit.get());
            return existingCircuit.get();
        }
        Circuit newCircuit = new Circuit();
        newCircuit.setName(name);
        newCircuit.setLengthMeters(new BigDecimal(circuitJson.get("length").getAsString()));
        newCircuit.setCountry(circuitJson.get("country").getAsString());
        Circuit saved = circuitRepository.save(newCircuit);
        circuitCache.put(name, saved);
        return saved;
    }

    /**
     * Finds or creates an event with the given data.
     *
     * @param eventName the name of the event
     * @param year      the year of the event
     * @param seriesId  the ID of the series
     * @param startDate the start date of the event
     * @param endDate   the end date of the event
     * @return the event entity
     */
    private Event findOrCreateEvent(String eventName, Integer year, Long seriesId, LocalDate startDate, LocalDate endDate) {
        String key = eventName + ":" + year + ":" + seriesId;
        Event cached = eventCache.getIfPresent(key);
        if (cached != null) return cached;
        Optional<Event> existingEvent = eventRepository.findByNameAndYear(eventName, year);
        if (existingEvent.isPresent()) {
            eventCache.put(key, existingEvent.get());
            return existingEvent.get();
        }
        Event newEvent = new Event();
        newEvent.setName(eventName);
        newEvent.setYear(year);
        newEvent.setSeriesId(seriesId);
        newEvent.setStartDate(startDate);
        newEvent.setEndDate(endDate);
        Event saved = eventRepository.save(newEvent);
        eventCache.put(key, saved);
        return saved;
    }

    /**
     * Creates a session with the given data.
     *
     * @param sessionJson the JSON data for the session
     * @param eventId     the ID of the event
     * @param circuitId   the ID of the circuit
     * @return the session entity
     */
    private Session createSession(JsonObject sessionJson, Long eventId, Long circuitId) {
        Session newSession = new Session();
        newSession.setEventId(eventId);
        newSession.setCircuitId(circuitId);
        newSession.setName(sessionJson.get("session_name").getAsString());
        newSession.setType(sessionJson.get("session_type").getAsString());
        String sessionDateStr = sessionJson.get("session_date").getAsString();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        LocalDateTime startDateTime = LocalDateTime.parse(sessionDateStr, formatter);
        newSession.setStartDatetime(startDateTime);
        int durationSeconds;
        if (sessionJson.has("finalize_type") && sessionJson.get("finalize_type").isJsonObject()) {
            JsonObject finalizeType = sessionJson.getAsJsonObject("finalize_type");
            if (finalizeType.has("time_in_seconds")) {
                durationSeconds = finalizeType.get("time_in_seconds").getAsInt();
                LOGGER.info("Parsed session duration: {} seconds ({} hours)", durationSeconds, durationSeconds / 3600.0);
            } else {
                throw new IllegalArgumentException("finalize_type object found but time_in_seconds field is missing");
            }
        } else {
            throw new IllegalArgumentException("finalize_type object not found in session JSON");
        }
        newSession.setDurationSeconds(durationSeconds);
        LOGGER.info("Session parsed: " + newSession);
        Session saved = sessionRepository.save(newSession);
        return saved;
    }

    /**
     * Processes the participants data.
     *
     * @param participantsJson the JSON data for the participants
     * @param seriesId         the ID of the series
     * @param sessionId        the ID of the session
     */
    private void processParticipants(JsonArray participantsJson, Long seriesId, Long sessionId) {
        // Get session to access duration and start date
        Optional<Session> sessionOpt = sessionRepository.findById(sessionId);
        if (!sessionOpt.isPresent()) {
            throw new IllegalArgumentException("Session not found with ID: " + sessionId);
        }
        Session session = sessionOpt.get();
        
        for (JsonElement participantElement : participantsJson) {
            JsonObject participantJson = participantElement.getAsJsonObject();

            // Find or create team
            Team team = findOrCreateTeam(participantJson.get("team").getAsString());
            LOGGER.info(team.toString());

            // Find or create class
            Class carClass = findOrCreateClass(seriesId, participantJson.get("class").getAsString());
            LOGGER.info(carClass.toString());

            // Create car entry
            CarEntry carEntry = createCarEntry(participantJson, sessionId, team.getId(),
                    carClass.getId());
            LOGGER.info(carEntry.toString());

            // Process drivers
            List<CarDriver> carDrivers = new ArrayList<>();
            if (participantJson.has("drivers") && participantJson.get("drivers").isJsonArray()) {
                JsonArray driversJson = participantJson.getAsJsonArray("drivers");
                carDrivers.addAll(processDrivers(driversJson, carEntry.getId()));
            }

            // Process laps
            if (participantJson.has("laps") && participantJson.get("laps").isJsonArray()) {
                JsonArray lapsJson = participantJson.getAsJsonArray("laps");
                processLaps(lapsJson, carEntry.getId(), carDrivers, session.getDurationSeconds(), session.getStartDatetime());
            }
        }
    }

    /**
     * Finds or creates a team with the given name.
     *
     * @param teamName the name of the team
     * @return the team entity
     */
    private Team findOrCreateTeam(String teamName) {
        Team cached = teamCache.getIfPresent(teamName);
        if (cached != null) return cached;
        Optional<Team> existingTeam = teamRepository.findByName(teamName);
        if (existingTeam.isPresent()) {
            teamCache.put(teamName, existingTeam.get());
            return existingTeam.get();
        }
        Team newTeam = new Team();
        newTeam.setName(teamName);
        Team saved = teamRepository.save(newTeam);
        teamCache.put(teamName, saved);
        return saved;
    }

    /**
     * Finds or creates a class with the given name.
     *
     * @param className the name of the class
     * @return the class entity
     */
    private Class findOrCreateClass(Long seriesId, String className) {
        String key = seriesId + ":" + className;
        Class cached = classCache.getIfPresent(key);
        if (cached != null) return cached;
        Optional<Class> existingClass = classRepository.findBySeriesIdAndName(seriesId, className);
        if (existingClass.isPresent()) {
            classCache.put(key, existingClass.get());
            return existingClass.get();
        }
        Class newClass = new Class();
        newClass.setSeriesId(seriesId);
        newClass.setName(className);
        Class saved = classRepository.save(newClass);
        classCache.put(key, saved);
        return saved;
    }

    /**
     * Creates a car entry with the given data.
     *
     * @param participantJson the JSON data for the participant
     * @param sessionId       the ID of the session
     * @param teamId          the ID of the team
     * @param classId         the ID of the class
     * @return the car entry entity
     */
    private CarEntry createCarEntry(JsonObject participantJson, Long sessionId, Long teamId,
                                    Long classId) {
        // Parse car number safely, handling both string and integer formats
        String carNumber = parseJsonNumberAsString(participantJson.get("number"), "car number");
        // Check if car entry already exists for this session and car number
        Optional<CarEntry> existingCarEntry = carEntryRepository.findBySessionIdAndNumber(sessionId, carNumber);
        if (existingCarEntry.isPresent()) {
            return existingCarEntry.get();
        }

        String vehicleModel = participantJson.get("vehicle").getAsString();
        // Find or create car model
        CarModel carModel = findOrCreateCarModel(vehicleModel);

        // Create car entry
        CarEntry newCarEntry = new CarEntry();
        newCarEntry.setSessionId(sessionId);
        newCarEntry.setTeamId(teamId);
        newCarEntry.setClassId(classId);
        newCarEntry.setCarModelId(carModel.getId());
        newCarEntry.setNumber(carNumber);

        if (participantJson.has("tires") && !participantJson.get("tires").isJsonNull()) {
            newCarEntry.setTireSupplier(participantJson.get("tires").getAsString());
        }

        LOGGER.info("Created car entry: {} - {}", carNumber, vehicleModel);
        return carEntryRepository.save(newCarEntry);
    }

    /**
     * Finds or creates a car model with the given data.
     *
     * @param vehicleModel   the vehicle model name
     * @return the car model entity
     */
    private CarModel findOrCreateCarModel(String vehicleModel) {
        String key = vehicleModel;
        CarModel cached = carModelCache.getIfPresent(key);
        if (cached != null) return cached;
        Optional<CarModel> existingCarModel = carModelRepository.findByName(vehicleModel);
        if (existingCarModel.isPresent()) {
            carModelCache.put(key, existingCarModel.get());
            return existingCarModel.get();
        }
        CarModel newCarModel = new CarModel();
        newCarModel.setName(vehicleModel);
        newCarModel.setFullName(vehicleModel);
        newCarModel.setYearModel(null);
        newCarModel.setDescription(null);
        CarModel saved = carModelRepository.save(newCarModel);
        carModelCache.put(key, saved);
        return saved;
    }

    /**
     * Processes the drivers data.
     *
     * @param driversJson the JSON data for the drivers
     * @param carEntryId       the ID of the car
     */
    private List<CarDriver> processDrivers(JsonArray driversJson, Long carEntryId) {
        List<CarDriver> carDriverList = new ArrayList<>();
        for (JsonElement driverElement : driversJson) {
            JsonObject driverJson = driverElement.getAsJsonObject();

            // Find or create driver
            Driver driver = findOrCreateDriver(driverJson);
            LOGGER.info(driver.toString());

            // Parse driver number safely, handling both string and integer formats
            int driverNumber = parseJsonNumberAsInt(driverJson.get("number"), "driver number");

            // Create car-driver association
            carDriverList.add(createCarDriver(carEntryId, driver.getId(), driverNumber));
        }
        return carDriverList;
    }

    /**
     * Finds or creates a driver with the given data.
     *
     * @param driverJson the JSON data for the driver
     * @return the driver entity
     */
    private Driver findOrCreateDriver(JsonObject driverJson) {
        String firstName = driverJson.get("firstname").getAsString();
        String lastName = driverJson.get("surname").getAsString();

        Optional<Driver> existingDriver = driverRepository.findByFirstNameAndLastName(firstName, lastName);
        if (existingDriver.isPresent()) {
            return existingDriver.get();
        }

        Driver newDriver = new Driver();
        newDriver.setFirstName(firstName);
        newDriver.setLastName(lastName);

        if (driverJson.has("country") && !driverJson.get("country").isJsonNull()) {
            newDriver.setNationality(driverJson.get("country").getAsString());
        }

        if (driverJson.has("hometown") && !driverJson.get("hometown").isJsonNull()) {
            newDriver.setHometown(driverJson.get("hometown").getAsString());
        }

        if (driverJson.has("license") && !driverJson.get("license").isJsonNull()) {
            newDriver.setLicenseType(driverJson.get("license").getAsString());
        }

        return driverRepository.save(newDriver);
    }

    /**
     * Creates a car-driver association.
     *
     * @param carEntryId        the ID of the car
     * @param driverId     the ID of the driver
     * @param driverNumber the driver number
     * @return the car-driver entity
     */
    private CarDriver createCarDriver(Long carEntryId, Long driverId, Integer driverNumber) {
        // Check if association already exists
        Optional<CarDriver> existingCarDriver = carDriverRepository.findByCarIdAndDriverId(carEntryId, driverId);
        if (existingCarDriver.isPresent()) {
            return existingCarDriver.get();
        }

        CarDriver newCarDriver = new CarDriver();
        newCarDriver.setCarId(carEntryId);
        newCarDriver.setDriverId(driverId);
        newCarDriver.setDriverNumber(driverNumber);

        return carDriverRepository.save(newCarDriver);
    }

    /**
     * Processes the laps data.
     *
     * @param lapsJson the JSON data for the laps
     * @param carId    the ID of the car
     * @param sessionDurationSeconds the duration of the session in seconds
     * @param sessionStartDateTime the start date and time of the session
     */
    private void processLaps(JsonArray lapsJson, Long carId, List<CarDriver> carDrivers, Integer sessionDurationSeconds, LocalDateTime sessionStartDateTime) {
        List<Lap> lapsToInsert = new ArrayList<>();
        List<JsonObject> lapJsons = new ArrayList<>();

        // First, parse all laps and collect unsaved Lap entities
        for (JsonElement lapElement : lapsJson) {
            JsonObject lapJson = lapElement.getAsJsonObject();
            int driverNumber = parseJsonNumberAsInt(lapJson.get("driver_number"), "driver_number");
            Optional<CarDriver> carDriver = carDrivers.stream().filter(cd -> cd.getDriverNumber() == driverNumber).findFirst();
            if (!carDriver.isPresent()) {
                LOGGER.warn("No car-driver association found for driver number {} in lap {}", driverNumber, lapJson);
                continue;
            }
            Lap lap = createLapUnsaved(lapJson, carId, carDriver.get().getDriverId(), sessionDurationSeconds, sessionStartDateTime);
            lapsToInsert.add(lap);
            lapJsons.add(lapJson);
        }
        // Batch insert all laps
        List<Lap> savedLaps = lapRepository.saveAll(lapsToInsert);
        // Now process sectors for each lap
        List<Sector> sectorsToInsert = new ArrayList<>();
        for (int i = 0; i < savedLaps.size(); i++) {
            Lap lap = savedLaps.get(i);
            JsonObject lapJson = lapJsons.get(i);
            if (lapJson.has("sector_times") && lapJson.get("sector_times").isJsonArray()) {
                JsonArray sectorTimesJson = lapJson.getAsJsonArray("sector_times");
                List<Sector> sectors = processSectorTimesUnsaved(sectorTimesJson, lap.getId());
                sectorsToInsert.addAll(sectors);
            }
        }
        // Batch insert all sectors
        sectorRepository.saveAll(sectorsToInsert);
    }

    // Utility method to create an unsaved Lap entity
    private Lap createLapUnsaved(JsonObject lapJson, Long carId, Long driverId, Integer sessionDurationSeconds, LocalDateTime sessionStartDateTime) {
        int lapNumber = parseJsonNumberAsInt(lapJson.get("number"), "lap number");
        Lap newLap = new Lap();
        newLap.setCarId(carId);
        newLap.setDriverId(driverId);
        newLap.setLapNumber(lapNumber);
        String lapTimeStr = lapJson.get("time").getAsString();
        newLap.setLapTimeSeconds(parseLapTime(lapTimeStr));
        String sessionElapsedStr = lapJson.get("session_elapsed").getAsString();
        newLap.setSessionElapsedSeconds(parseLapTime(sessionElapsedStr));
        String timestampStr = lapJson.get("hour").getAsString();
        LocalDateTime timestamp = parseTimestamp(timestampStr, sessionDurationSeconds, sessionStartDateTime);
        newLap.setTimestamp(timestamp);
        if (lapJson.has("average_speed_kph") && !lapJson.get("average_speed_kph").isJsonNull()) {
            newLap.setAverageSpeedKph(new BigDecimal(lapJson.get("average_speed_kph").getAsString()));
        }
        return newLap;
    }

    // Utility method to process sector times and return unsaved entities
    private List<Sector> processSectorTimesUnsaved(JsonArray sectorTimesJson, Long lapId) {
        List<Sector> sectors = new ArrayList<>();
        for (JsonElement sectorElement : sectorTimesJson) {
            JsonObject sectorJson = sectorElement.getAsJsonObject();
            Sector sector = createSectorUnsaved(sectorJson, lapId);
            sectors.add(sector);
        }
        return sectors;
    }

    // Utility method to create an unsaved Sector entity
    private Sector createSectorUnsaved(JsonObject sectorJson, Long lapId) {
        int sectorNumber = parseJsonNumberAsInt(sectorJson.get("index"), "sector index");
        Sector newSector = new Sector();
        newSector.setLapId(lapId);
        newSector.setSectorNumber(sectorNumber);
        String sectorTimeStr = sectorJson.get("time").getAsString();
        BigDecimal sectorTime = parseSectorTime(sectorTimeStr);
        newSector.setSectorTimeSeconds(sectorTime);
        return newSector;
    }

    /**
     * Parses a lap time string (e.g., "1:48.656") to seconds.
     *
     * @param lapTimeStr the lap time string
     * @return the lap time in seconds as a BigDecimal
     */
    private BigDecimal parseLapTime(String lapTimeStr) {
        String[] parts = lapTimeStr.split(":");
        if (parts.length != 2) {
            return BigDecimal.ZERO;
        }

        int minutes = Integer.parseInt(parts[0]);
        double seconds = Double.parseDouble(parts[1]);

        return BigDecimal.valueOf(minutes * 60 + seconds);
    }

    /**
     * Parses a timestamp string, handling both 24-hour and shorter races.
     * For 24-hour races (session duration >= 24 hours), expects full date-time format.
     * For shorter races, expects 24-hour time format without AM/PM.
     *
     * @param timestampStr           the timestamp string to parse
     * @param sessionDurationSeconds the duration of the session in seconds
     * @param sessionStartDateTime   the start date and time of the session
     * @return the parsed LocalDateTime
     */
    private LocalDateTime parseTimestamp(String timestampStr, Integer sessionDurationSeconds, LocalDateTime sessionStartDateTime) {
        // Check if this is a 24-hour race (24 hours = 86400 seconds)
        boolean is24HourRace = sessionDurationSeconds >= 86400;

        if (is24HourRace) {
            // For 24-hour races, expect full date-time format: "M/d/yyyy h:mm:ss a"
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy h:mm:ss a");
            return LocalDateTime.parse(timestampStr, formatter);
        } else {
            // For shorter races, expect 24-hour time format: "HH:mm:ss"
            try {
                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
                LocalTime time = LocalTime.parse(timestampStr, timeFormatter);
                // Use the actual session start date
                return LocalDateTime.of(sessionStartDateTime.toLocalDate(), time);
            } catch (Exception e) {
                // If 24-hour time parsing fails, try the original full date-time format as fallback
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy h:mm:ss a");
                    return LocalDateTime.parse(timestampStr, formatter);
                } catch (Exception e2) {
                    // If all parsing fails, use session start time as fallback
                    LOGGER.warn("Failed to parse timestamp '{}' for session duration {} seconds, using session start time",
                            timestampStr, sessionDurationSeconds);
                    return sessionStartDateTime;
                }
            }
        }
    }

    BigDecimal parseSectorTime(String sectorTimeStr) {
        try {
            if (sectorTimeStr == null || sectorTimeStr.trim().isEmpty()) {
                return null;  // Return null for blank values
            }

            // Check for unreasonably large values (e.g., starting with numbers like 48274)
            if (sectorTimeStr.matches("^[0-9]{5,}.*")) {
                return null;  // Return null for unreasonably large values
            }

            if (sectorTimeStr.contains(":")) {
                String[] parts = sectorTimeStr.split(":");
                if (parts.length == 2) {
                    // Format: MM:SS.SSS
                    int minutes = Integer.parseInt(parts[0]);
                    double seconds = Double.parseDouble(parts[1]);
                    double totalSeconds = minutes * 60 + seconds;
                    // Check if the time is reasonable (less than 1 hour)
                    return totalSeconds < 3600 ? BigDecimal.valueOf(totalSeconds) : null;
                } else if (parts.length == 3) {
                    // Format: HH:MM:SS.SSS
                    int hours = Integer.parseInt(parts[0]);
                    int minutes = Integer.parseInt(parts[1]);
                    double seconds = Double.parseDouble(parts[2]);
                    double totalSeconds = (hours * 3600) + (minutes * 60) + seconds;
                    // Check if the time is reasonable (less than 1 hour)
                    return totalSeconds < 3600 ? BigDecimal.valueOf(totalSeconds) : null;
                }
            } else {
                // Format: SS.SSS
                double seconds = Double.parseDouble(sectorTimeStr);
                // Check if the time is reasonable (less than 1 hour)
                return seconds < 3600 ? BigDecimal.valueOf(seconds) : null;
            }
            return null;
        } catch (Exception e) {
            return null;  // Return null for any parsing errors
        }
    }

    /**
     * Safely converts a JSON element to a string, handling both string and number types.
     *
     * @param element   the JSON element to convert
     * @param fieldName the name of the field for error messages
     * @return the string representation of the element
     * @throws IllegalArgumentException if the element cannot be converted to a string
     */
    private String parseJsonNumberAsString(JsonElement element, String fieldName) {
        if (element == null || element.isJsonNull()) {
            throw new IllegalArgumentException(fieldName + " field cannot be null or empty");
        }

        if (element.isJsonPrimitive()) {
            JsonPrimitive primitive = element.getAsJsonPrimitive();
            if (primitive.isString()) {
                String value = primitive.getAsString();
                if (value.trim().isEmpty()) {
                    throw new IllegalArgumentException(fieldName + " field cannot be empty");
                }
                return value;
            } else if (primitive.isNumber()) {
                return String.valueOf(primitive.getAsInt());
            }
        }

        throw new IllegalArgumentException(fieldName + " field must be a string or number, got: " + element.getClass().getSimpleName());
    }

    /**
     * Safely converts a JSON element to an integer, handling both string and number types.
     *
     * @param element   the JSON element to convert
     * @param fieldName the name of the field for error messages
     * @return the integer value
     * @throws IllegalArgumentException if the element cannot be converted to an integer
     */
    private int parseJsonNumberAsInt(JsonElement element, String fieldName) {
        if (element == null || element.isJsonNull()) {
            throw new IllegalArgumentException(fieldName + " field cannot be null or empty");
        }

        if (element.isJsonPrimitive()) {
            JsonPrimitive primitive = element.getAsJsonPrimitive();
            if (primitive.isNumber()) {
                int value = primitive.getAsInt();
                if (value < 0) {
                    throw new IllegalArgumentException(fieldName + " field cannot be negative: " + value);
                }
                return value;
            } else if (primitive.isString()) {
                try {
                    int value = Integer.parseInt(primitive.getAsString().trim());
                    if (value < 0) {
                        throw new IllegalArgumentException(fieldName + " field cannot be negative: " + value);
                    }
                    return value;
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException(fieldName + " field must be a valid integer, got: " + primitive.getAsString());
                }
            }
        }

        throw new IllegalArgumentException(fieldName + " field must be a string or number, got: " + element.getClass().getSimpleName());
    }

    @Override
    public ProcessResultsResponseDTO processResultsCsv(ProcessResultsRequestDTO request) {
        var importType = request.getImportType();
        try {
            // 1. Download the CSV file using OkHttp
            Request httpRequest = new Request.Builder()
                    .url(request.getResultsUrl())
                    .build();
            try (Response response = httpClient.newCall(httpRequest).execute()) {
                if (!response.isSuccessful() || response.body() == null) {
                    return new ProcessResultsResponseDTO(null, "FAILED", "Failed to fetch CSV: HTTP " + (response != null ? response.code() : "null response"));
                }
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.body().byteStream(), StandardCharsets.UTF_8));

                // 2. Parse the CSV header
                String headerLine = reader.readLine();
                if (headerLine == null) {
                    return new ProcessResultsResponseDTO(null, "FAILED", "CSV is empty");
                }
                String[] headers = headerLine.split(";");

                // 3. Ensure session exist (using provided metadata)
                Session session;
                if (request.getSessionId() != null) {
                    session = sessionRepository.findById(request.getSessionId())
                            .orElseThrow(() -> new IllegalArgumentException("Session not found: " + request.getSessionId()));
                } else {
                    throw new IllegalArgumentException("sessionId is required in the request");
                }

                var event = eventRepository.findById(session.getEventId()).get();

                // 4. For each row, ensure car model, car entry, team, drivers exist
                String row;
                while ((row = reader.readLine()) != null) {
                    if (row.trim().isEmpty()) continue;
                    String[] values = row.split(";");

                    // --- Parse team, class, car model, car number, tire supplier ---
                    String teamName = getValueByHeader(headers, values, "TEAM");
                    String className = getValueByHeader(headers, values, "CLASS");
                    String carModelName = getValueByHeader(headers, values, "VEHICLE");
                    String carNumber = getValueByHeader(headers, values, "NUMBER");
                    String tireSupplier = getValueByHeader(headers, values,
                            importType.equals(ProcessResultsRequestDTO.ImportType.WEC) ? "TYRES" : "TIRES");

                    // --- Find or create team, class, car model ---
                    Team team = findOrCreateTeam(teamName);
                    Class carClass = findOrCreateClass(event.getSeriesId(), className);
                    CarModel carModel = findOrCreateCarModel(carModelName);

                    // --- Create car entry ---
                    CarEntry carEntry = findOrCreateCarEntry(session.getId(), team.getId(), carClass.getId(), carModel.getId(), carNumber, tireSupplier);

                    // --- Parse and create drivers ---
                    for (int i = 1; i <= 6; i++) { // Support up to 6 drivers (IMSA)
                        String[] names;
                        if (importType == ProcessResultsRequestDTO.ImportType.WEC) {
                            String driverCol = "DRIVER_" + i;
                            String driverName = getValueByHeader(headers, values, driverCol);
                            if (driverName == null || driverName.isBlank()) continue;
                            names = parseWECName(driverName);
                        } else if (importType == ProcessResultsRequestDTO.ImportType.IMSA) { // IMSA splits up names into two columns
                            String driverFirstNameCol = String.format("DRIVER%d_FIRSTNAME", i);
                            String driverSecondNameCol = String.format("DRIVER%d_SECONDNAME", i);
                            String driverFirstName = getValueByHeader(headers, values, driverFirstNameCol);
                            String driverSecondName = getValueByHeader(headers, values, driverSecondNameCol);

                            if (driverFirstName == null || driverFirstName.isBlank()
                                    || driverSecondName == null || driverSecondName.isBlank()) continue;
                            names = new String[]{driverFirstName, driverSecondName};
                        } else {
                            throw new IllegalArgumentException("Unsupported importer type: " + importType);
                        }
                        Driver driver = findOrCreateDriverFromCsvName(names[0], names[1]);
                        // Create car-driver association
                        createCarDriver(carEntry.getId(), driver.getId(), i);
                    }
                }
                reader.close();
                return new ProcessResultsResponseDTO(session.getId(), "SUCCESS", null);
            }
        } catch (Exception e) {
            LOGGER.error("Failed to process results CSV", e);
            return new ProcessResultsResponseDTO(null, "FAILED", e.getMessage());
        }
    }

    public static String[] parseWECName(String fullName) {
        String[] parts = fullName.split(" ");
        StringBuilder firstName = new StringBuilder();
        StringBuilder lastName = new StringBuilder();

        for (String part : parts) {
            // Check if the part is fully uppercase (indicating last name)
            if (part.equals(part.toUpperCase()) && !part.isEmpty()) {
                // Add space if not the first part of the last name
                if (!lastName.isEmpty()) {
                    lastName.append(" ");
                }
                lastName.append(part);
            } else {
                // Add space if not the first part of the first name
                if (!firstName.isEmpty()) {
                    firstName.append(" ");
                }
                firstName.append(part);
            }
        }

        return new String[]{firstName.toString(), lastName.toString()};
    }


    // Helper to robustly get a value by header name
    private String getValueByHeader(String[] headers, String[] values, String key) {
        for (int i = 0; i < headers.length; i++) {
            if (headers[i].trim().equalsIgnoreCase(key) && i < values.length) {
                return values[i].trim();
            }
        }
        return null;
    }

    // Helper to find or create a car entry (by session, team, class, car model, number, tire supplier)
    private CarEntry findOrCreateCarEntry(Long sessionId, Long teamId, Long classId, Long carModelId, String carNumber, String tireSupplier) {
        Optional<CarEntry> existing = carEntryRepository.findBySessionIdAndNumber(sessionId, carNumber);
        if (existing.isPresent()) return existing.get();
        CarEntry entry = new CarEntry();
        entry.setSessionId(sessionId);
        entry.setTeamId(teamId);
        entry.setClassId(classId);
        entry.setCarModelId(carModelId);
        entry.setNumber(carNumber);
        entry.setTireSupplier(tireSupplier);
        return carEntryRepository.save(entry);
    }

    // Helper to find or create a driver from a CSV name ("Firstname Lastname")
    private Driver findOrCreateDriverFromCsvName(String firstName, String lastName) {
        Optional<Driver> existing = driverRepository.findByFirstNameAndLastName(firstName, lastName);
        if (existing.isPresent()) return existing.get();
        Driver driver = new Driver();
        driver.setFirstName(firstName);
        driver.setLastName(lastName);
        // Leave other fields blank as per requirements
        return driverRepository.save(driver);
    }
}
