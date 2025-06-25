package com.arjunakankipati.racingstatanalysis.service.impl;

import com.arjunakankipati.racingstatanalysis.dto.ImportRequestDTO;
import com.arjunakankipati.racingstatanalysis.dto.ImportResponseDTO;
import com.arjunakankipati.racingstatanalysis.exceptions.ReportURLNotValidException;
import com.arjunakankipati.racingstatanalysis.model.*;
import com.arjunakankipati.racingstatanalysis.model.Class;
import com.arjunakankipati.racingstatanalysis.repository.*;
import com.arjunakankipati.racingstatanalysis.service.ImportService;
import com.google.gson.*;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
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
    private final ManufacturerRepository manufacturerRepository;
    private final ClassRepository classRepository;
    private final DriverRepository driverRepository;
    private final CarEntryRepository carEntryRepository;
    private final CarModelRepository carModelRepository;
    private final CarDriverRepository carDriverRepository;
    private final LapRepository lapRepository;
    private final SectorRepository sectorRepository;

    private final OkHttpClient httpClient;
    private final Gson gson;

    /**
     * Constructor with repository dependency injection.
     */
    @Autowired
    public ImportServiceImpl(SeriesRepository seriesRepository,
                             EventRepository eventRepository,
                             CircuitRepository circuitRepository,
                             SessionRepository sessionRepository,
                             TeamRepository teamRepository,
                             ManufacturerRepository manufacturerRepository,
                             ClassRepository classRepository,
                             DriverRepository driverRepository,
                             CarEntryRepository carEntryRepository,
                             CarModelRepository carModelRepository,
                             CarDriverRepository carDriverRepository,
                             LapRepository lapRepository,
                             SectorRepository sectorRepository) {
        this.seriesRepository = seriesRepository;
        this.eventRepository = eventRepository;
        this.circuitRepository = circuitRepository;
        this.sessionRepository = sessionRepository;
        this.teamRepository = teamRepository;
        this.manufacturerRepository = manufacturerRepository;
        this.classRepository = classRepository;
        this.driverRepository = driverRepository;
        this.carEntryRepository = carEntryRepository;
        this.carModelRepository = carModelRepository;
        this.carDriverRepository = carDriverRepository;
        this.lapRepository = lapRepository;
        this.sectorRepository = sectorRepository;

        // Initialize OkHttpClient with reasonable timeouts
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();

        // Initialize Gson with custom adapters for LocalDate and LocalDateTime
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, (JsonDeserializer<LocalDate>)
                        (json, type, context) -> LocalDate.parse(json.getAsString(),
                                DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                .registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>)
                        (json, type, context) -> {
                            String dateStr = json.getAsString();
                            // Handle different date formats
                            if (dateStr.contains("/")) {
                                return LocalDateTime.parse(dateStr,
                                        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
                            } else {
                                return LocalDateTime.parse(dateStr,
                                        DateTimeFormatter.ofPattern("M/d/yyyy h:mm:ss a"));
                            }
                        })
                .create();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ImportResponseDTO importFromUrl(ImportRequestDTO request) {
        long startTimeMillis = System.currentTimeMillis();
        try {
            // Validate request
            if (request == null) {
                throw new IllegalArgumentException("Import request cannot be null");
            }
            if (request.getUrl() == null || request.getUrl().trim().isEmpty()) {
                throw new IllegalArgumentException("URL cannot be null or empty");
            }

            // Generate a unique import ID
            String importId = UUID.randomUUID().toString();

            // Fetch JSON data from URL
            JsonObject jsonData = fetchJsonData(request.getUrl());

            // Validate JSON structure
            validateJsonStructure(jsonData);

            // Process and store the data
            processData(jsonData, request.getUrl());

            long endTimeMillis = System.currentTimeMillis();

            // Return response
            return new ImportResponseDTO(
                    importId,
                    "completed",
                    (endTimeMillis - startTimeMillis) / 1000
            );
        } catch (Exception e) {
            // Log the error
            e.printStackTrace();

            long endTimeMillis = System.currentTimeMillis();
            // Return error response
            return new ImportResponseDTO(
                    UUID.randomUUID().toString(),
                    "failed: " + e.getMessage(),
                    (endTimeMillis - startTimeMillis) / 1000
            );
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
                throw new ReportURLNotValidException("Invalid JSON format in response", e);
            }
        }
    }

    /**
     * Validates the JSON structure to ensure it contains the required fields.
     *
     * @param jsonData the JSON data to validate
     * @throws ReportURLNotValidException if the JSON structure is not valid
     */
    private void validateJsonStructure(JsonObject jsonData) {
        // Check if the JSON has the required top-level fields
        if (!jsonData.has("session") || !jsonData.has("participants")) {
            throw new ReportURLNotValidException("JSON data must contain 'session' and 'participants' fields");
        }

        // Check if the session field is an object
        if (!jsonData.get("session").isJsonObject()) {
            throw new ReportURLNotValidException("'session' field must be an object");
        }

        // Check if the participants field is an array
        if (!jsonData.get("participants").isJsonArray()) {
            throw new ReportURLNotValidException("'participants' field must be an array");
        }

        // Check if the session object has the required fields
        JsonObject session = jsonData.getAsJsonObject("session");
        String[] requiredSessionFields = {"championship_name", "event_name", "session_name", "session_type", "session_date"};
        for (String field : requiredSessionFields) {
            if (!session.has(field)) {
                throw new ReportURLNotValidException("Session object must contain '" + field + "' field");
            }
        }

        // Check if the session object has a circuit object with required fields
        if (!session.has("circuit") || !session.get("circuit").isJsonObject()) {
            throw new ReportURLNotValidException("Session object must contain a 'circuit' object");
        }

        JsonObject circuit = session.getAsJsonObject("circuit");
        String[] requiredCircuitFields = {"name", "length", "country"};
        for (String field : requiredCircuitFields) {
            if (!circuit.has(field)) {
                throw new ReportURLNotValidException("Circuit object must contain '" + field + "' field");
            }
        }

        // Validate finalize_type structure
        if (!session.has("finalize_type") || !session.get("finalize_type").isJsonObject()) {
            throw new ReportURLNotValidException("Session object must contain a 'finalize_type' object");
        }

        JsonObject finalizeType = session.getAsJsonObject("finalize_type");
        if (!finalizeType.has("time_in_seconds")) {
            throw new ReportURLNotValidException("finalize_type object must contain 'time_in_seconds' field");
        }

        // Validate participants structure
        JsonArray participants = jsonData.getAsJsonArray("participants");
        for (JsonElement participantElement : participants) {
            if (!participantElement.isJsonObject()) {
                throw new ReportURLNotValidException("Each participant must be an object");
            }

            JsonObject participant = participantElement.getAsJsonObject();

            // Check required participant fields
            String[] requiredParticipantFields = {"number", "team", "class", "vehicle", "manufacturer"};
            for (String field : requiredParticipantFields) {
                if (!participant.has(field)) {
                    throw new ReportURLNotValidException("Participant object must contain '" + field + "' field");
                }
            }

            // Validate that number field is not null or empty
            JsonElement numberElement = participant.get("number");
            if (numberElement == null || numberElement.isJsonNull()) {
                throw new ReportURLNotValidException("Participant number field cannot be null");
            }

            // Validate drivers array if present
            if (participant.has("drivers") && participant.get("drivers").isJsonArray()) {
                JsonArray drivers = participant.getAsJsonArray("drivers");
                for (JsonElement driverElement : drivers) {
                    if (!driverElement.isJsonObject()) {
                        throw new ReportURLNotValidException("Each driver must be an object");
                    }

                    JsonObject driver = driverElement.getAsJsonObject();
                    String[] requiredDriverFields = {"number", "firstname", "surname"};
                    for (String field : requiredDriverFields) {
                        if (!driver.has(field)) {
                            throw new ReportURLNotValidException("Driver object must contain '" + field + "' field");
                        }
                    }

                    // Validate that driver number field is not null or empty
                    JsonElement driverNumberElement = driver.get("number");
                    if (driverNumberElement == null || driverNumberElement.isJsonNull()) {
                        throw new ReportURLNotValidException("Driver number field cannot be null");
                    }
                }
            }
        }
    }

    /**
     * Processes and stores the data from the JSON.
     *
     * @param jsonData the JSON data to process
     * @param url      the URL of the imported data
     */
    private void processData(JsonObject jsonData, String url) {
        // Extract session data
        JsonObject sessionJson = jsonData.getAsJsonObject("session");
        JsonObject circuitJson = sessionJson.getAsJsonObject("circuit");

        // Extract series name, event name, and year from the JSON data
        String seriesName = sessionJson.get("championship_name").getAsString();
        String eventName = sessionJson.get("event_name").getAsString();

        // Extract year from session_date (format: "25/01/2025 01:40")
        String sessionDateStr = sessionJson.get("session_date").getAsString();
        int year = Integer.parseInt(sessionDateStr.substring(6, 10));

        // Find or create series
        Series series = findOrCreateSeries(seriesName);
        LOGGER.info(series.toString());

        // Find or create circuit
        Circuit circuit = findOrCreateCircuit(circuitJson);
        LOGGER.info(circuit.toString());

        // Find or create event
        Event event = findOrCreateEvent(eventName, year, series.getId());
        LOGGER.info(event.toString());

        // Create session
        Session session = createSession(sessionJson, event.getId(), circuit.getId(), url);
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
        Optional<Series> existingSeries = seriesRepository.findByName(seriesName);
        if (existingSeries.isPresent()) {
            return existingSeries.get();
        }

        Series newSeries = new Series();
        newSeries.setName(seriesName);
        return seriesRepository.save(newSeries);
    }

    /**
     * Finds or creates a circuit with the given data.
     *
     * @param circuitJson the JSON data for the circuit
     * @return the circuit entity
     */
    private Circuit findOrCreateCircuit(JsonObject circuitJson) {
        String name = circuitJson.get("name").getAsString();
        Optional<Circuit> existingCircuit = circuitRepository.findByName(name);
        if (existingCircuit.isPresent()) {
            return existingCircuit.get();
        }

        Circuit newCircuit = new Circuit();
        newCircuit.setName(name);
        newCircuit.setLengthMeters(new BigDecimal(circuitJson.get("length").getAsString()));
        newCircuit.setCountry(circuitJson.get("country").getAsString());
        return circuitRepository.save(newCircuit);
    }

    /**
     * Finds or creates an event with the given data.
     *
     * @param eventName the name of the event
     * @param year      the year of the event
     * @param seriesId  the ID of the series
     * @return the event entity
     */
    private Event findOrCreateEvent(String eventName, Integer year, Long seriesId) {
        // TODO: this should have a filter on series id as well probably
        Optional<Event> existingEvent = eventRepository.findByNameAndYear(eventName, year);
        if (existingEvent.isPresent()) {
            return existingEvent.get();
        }

        Event newEvent = new Event();
        newEvent.setName(eventName);
        newEvent.setYear(year);
        newEvent.setSeriesId(seriesId);
        // TODO: this should be pulling from the actual dates
        newEvent.setStartDate(LocalDate.now()); // Default to current date
        newEvent.setEndDate(LocalDate.now().plusDays(1)); // Default to next day
        return eventRepository.save(newEvent);
    }

    /**
     * Creates a session with the given data.
     *
     * @param sessionJson the JSON data for the session
     * @param eventId     the ID of the event
     * @param circuitId   the ID of the circuit
     * @param importUrl   the URL of the imported data
     * @return the session entity
     */
    private Session createSession(JsonObject sessionJson, Long eventId, Long circuitId, String importUrl) {
        // Check if a session with this import URL already exists
        Optional<Session> existingSession = sessionRepository.findByImportUrl(importUrl);
        if (existingSession.isPresent()) {
            return existingSession.get();
        }

        Session newSession = new Session();
        newSession.setEventId(eventId);
        newSession.setCircuitId(circuitId);
        newSession.setName(sessionJson.get("session_name").getAsString());
        newSession.setType(sessionJson.get("session_type").getAsString());

        // Parse session date
        String sessionDateStr = sessionJson.get("session_date").getAsString();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        LocalDateTime startDateTime = LocalDateTime.parse(sessionDateStr, formatter);
        newSession.setStartDatetime(startDateTime);

        // Parse duration from finalize_type.time_in_seconds
        int durationSeconds;
        if (sessionJson.has("finalize_type") && sessionJson.get("finalize_type").isJsonObject()) {
            JsonObject finalizeType = sessionJson.getAsJsonObject("finalize_type");
            if (finalizeType.has("time_in_seconds")) {
                durationSeconds = finalizeType.get("time_in_seconds").getAsInt();
                LOGGER.info("Parsed session duration: {} seconds ({} hours)",
                        durationSeconds, durationSeconds / 3600.0);
            } else {
                throw new IllegalArgumentException("finalize_type object found but time_in_seconds field is missing");
            }
        } else {
            throw new IllegalArgumentException("finalize_type object not found in session JSON");
        }
        newSession.setDurationSeconds(durationSeconds);

        // Set weather data if available
        if (sessionJson.has("weather") && sessionJson.get("weather").isJsonObject()) {
            JsonObject weatherJson = sessionJson.getAsJsonObject("weather");
            if (weatherJson.has("air_temperature")) {
                String airTempStr = weatherJson.get("air_temperature").getAsString().replace(" ºC", "");
                newSession.setWeatherAirTemp(new BigDecimal(airTempStr));
            }
            if (weatherJson.has("track_temperature")) {
                String trackTempStr = weatherJson.get("track_temperature").getAsString().replace(" ºC", "");
                newSession.setWeatherTrackTemp(new BigDecimal(trackTempStr));
            }
            if (weatherJson.has("track_status")) {
                newSession.setWeatherCondition(weatherJson.get("track_status").getAsString());
            }
        }

        // Set report message if available
        if (sessionJson.has("report_message")) {
            newSession.setReportMessage(sessionJson.get("report_message").getAsString());
        }

        // Set import URL and timestamp
        newSession.setImportUrl(importUrl);
        newSession.setImportTimestamp(LocalDateTime.now());

        LOGGER.info("Session parsed: " + newSession);
        return sessionRepository.save(newSession);
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

            // Find or create manufacturer
            Manufacturer manufacturer = findOrCreateManufacturer(
                    participantJson.get("manufacturer").getAsString());
            LOGGER.info(manufacturer.toString());

            // Find or create class
            Class carClass = findOrCreateClass(seriesId, participantJson.get("class").getAsString());
            LOGGER.info(carClass.toString());

            // Create car
            CarEntry car = createCar(participantJson, sessionId, team.getId(),
                    carClass.getId(), manufacturer.getId());
            LOGGER.info(car.toString());

            // Process drivers
            if (participantJson.has("drivers") && participantJson.get("drivers").isJsonArray()) {
                JsonArray driversJson = participantJson.getAsJsonArray("drivers");
                processDrivers(driversJson, car.getId());
            }

            // Process laps
            if (participantJson.has("laps") && participantJson.get("laps").isJsonArray()) {
                JsonArray lapsJson = participantJson.getAsJsonArray("laps");
                processLaps(lapsJson, car.getId(), session.getDurationSeconds(), session.getStartDatetime());
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
        Optional<Team> existingTeam = teamRepository.findByName(teamName);
        if (existingTeam.isPresent()) {
            return existingTeam.get();
        }

        Team newTeam = new Team();
        newTeam.setName(teamName);
        return teamRepository.save(newTeam);
    }

    /**
     * Finds or creates a manufacturer with the given name.
     *
     * @param manufacturerName the name of the manufacturer
     * @return the manufacturer entity
     */
    private Manufacturer findOrCreateManufacturer(String manufacturerName) {
        Optional<Manufacturer> existingManufacturer = manufacturerRepository.findByName(manufacturerName);
        if (existingManufacturer.isPresent()) {
            return existingManufacturer.get();
        }

        Manufacturer newManufacturer = new Manufacturer();
        newManufacturer.setName(manufacturerName);
        return manufacturerRepository.save(newManufacturer);
    }

    /**
     * Finds or creates a class with the given name.
     *
     * @param className the name of the class
     * @return the class entity
     */
    private Class findOrCreateClass(Long seriesId, String className) {
        //TODO add series id filter
        Optional<Class> existingClass = classRepository.findByName(className);
        if (existingClass.isPresent()) {
            return existingClass.get();
        }

        Class newClass = new Class();
        newClass.setSeriesId(seriesId);
        newClass.setName(className);
        return classRepository.save(newClass);
    }

    /**
     * Creates a car entry with the given data.
     *
     * @param participantJson the JSON data for the participant
     * @param sessionId       the ID of the session
     * @param teamId          the ID of the team
     * @param classId         the ID of the class
     * @param manufacturerId  the ID of the manufacturer
     * @return the car entry entity
     */
    private CarEntry createCar(JsonObject participantJson, Long sessionId, Long teamId,
                          Long classId, Long manufacturerId) {
        // Parse car number safely, handling both string and integer formats
        String carNumber = parseJsonNumberAsString(participantJson.get("number"), "car number");
        validateCarNumber(carNumber);
        
        String vehicleModel = participantJson.get("vehicle").getAsString();

        // Check if car entry already exists for this session and car number
        Optional<CarEntry> existingCarEntry = carEntryRepository.findBySessionIdAndNumber(sessionId, carNumber);
        if (existingCarEntry.isPresent()) {
            return existingCarEntry.get();
        }

        // Find or create car model
        CarModel carModel = findOrCreateCarModel(vehicleModel, manufacturerId);

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
     * @param manufacturerId the ID of the manufacturer
     * @return the car model entity
     */
    private CarModel findOrCreateCarModel(String vehicleModel, Long manufacturerId) {
        // Try to find existing car model by manufacturer and name
        Optional<CarModel> existingCarModel = carModelRepository.findByManufacturerIdAndName(manufacturerId, vehicleModel);
        if (existingCarModel.isPresent()) {
            return existingCarModel.get();
        }

        // Create new car model
        CarModel newCarModel = new CarModel();
        newCarModel.setManufacturerId(manufacturerId);
        newCarModel.setName(vehicleModel);
        newCarModel.setFullName(vehicleModel); // For now, use the same name as full name
        newCarModel.setYearModel(null); // Year model not available in import data
        newCarModel.setDescription(null);

        return carModelRepository.save(newCarModel);
    }

    /**
     * Processes the drivers data.
     *
     * @param driversJson the JSON data for the drivers
     * @param carId       the ID of the car
     */
    private void processDrivers(JsonArray driversJson, Long carId) {
        for (JsonElement driverElement : driversJson) {
            JsonObject driverJson = driverElement.getAsJsonObject();

            // Find or create driver
            Driver driver = findOrCreateDriver(driverJson);
            LOGGER.info(driver.toString());

            // Parse driver number safely, handling both string and integer formats
            int driverNumber = parseJsonNumberAsInt(driverJson.get("number"), "driver number");

            // Create car-driver association
            createCarDriver(carId, driver.getId(), driverNumber);
        }
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
     * @param carId        the ID of the car
     * @param driverId     the ID of the driver
     * @param driverNumber the driver number
     * @return the car-driver entity
     */
    private CarDriver createCarDriver(Long carId, Long driverId, Integer driverNumber) {
        // Check if association already exists
        Optional<CarDriver> existingCarDriver = carDriverRepository.findByCarIdAndDriverId(carId, driverId);
        if (existingCarDriver.isPresent()) {
            return existingCarDriver.get();
        }

        CarDriver newCarDriver = new CarDriver();
        newCarDriver.setCarId(carId);
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
    private void processLaps(JsonArray lapsJson, Long carId, Integer sessionDurationSeconds, LocalDateTime sessionStartDateTime) {
        for (JsonElement lapElement : lapsJson) {
            JsonObject lapJson = lapElement.getAsJsonObject();

            // Parse driver number safely, handling both string and integer formats
            int driverNumber = parseJsonNumberAsInt(lapJson.get("driver_number"), "driver_number");

            // Find driver ID for this car and driver number
            Optional<CarDriver> carDriver = carDriverRepository.findByCarIdAndDriverNumber(carId, driverNumber);
            if (!carDriver.isPresent()) {
                // Skip this lap if driver not found
                continue;
            }

            // Create lap
            Lap lap = createLap(lapJson, carId, carDriver.get().getDriverId(), sessionDurationSeconds, sessionStartDateTime);
            LOGGER.info(lap.toString());

            // Process sector times
            if (lapJson.has("sector_times") && lapJson.get("sector_times").isJsonArray()) {
                JsonArray sectorTimesJson = lapJson.getAsJsonArray("sector_times");
                List<Sector> sectors = processSectorTimes(sectorTimesJson, lap.getId());

                // Update lap validity based on sector validity
                boolean allSectorsValid = true;
                for (Sector sector : sectors) {
                    if (sector.getIsValid() == null || !sector.getIsValid()) {
                        allSectorsValid = false;
                        break;
                    }
                }

                if (!allSectorsValid && lap.getIsValid()) {
                    lap.setIsValid(false);
                    lap.setInvalidationReason("One or more invalid sector times");
                    lapRepository.save(lap);
                    LOGGER.info("Lap " + lap.getLapNumber() + " invalidated due to invalid sector times");
                }
            }
        }
    }

    /**
     * Creates a lap with the given data.
     *
     * @param lapJson  the JSON data for the lap
     * @param carId    the ID of the car
     * @param driverId the ID of the driver
     * @param sessionDurationSeconds the duration of the session in seconds
     * @param sessionStartDateTime the start date and time of the session
     * @return the lap entity
     */
    private Lap createLap(JsonObject lapJson, Long carId, Long driverId, Integer sessionDurationSeconds, LocalDateTime sessionStartDateTime) {
        // Parse lap number safely, handling both string and integer formats
        int lapNumber = parseJsonNumberAsInt(lapJson.get("number"), "lap number");

        // Check if lap already exists for this car and lap number
        Optional<Lap> existingLap = lapRepository.findByCarIdAndLapNumber(carId, lapNumber);
        if (existingLap.isPresent()) {
            return existingLap.get();
        }

        Lap newLap = new Lap();
        newLap.setCarId(carId);
        newLap.setDriverId(driverId);
        newLap.setLapNumber(lapNumber);

        // Parse lap time
        String lapTimeStr = lapJson.get("time").getAsString();
        newLap.setLapTimeSeconds(parseLapTime(lapTimeStr));

        // Parse session elapsed time
        String sessionElapsedStr = lapJson.get("session_elapsed").getAsString();
        newLap.setSessionElapsedSeconds(parseLapTime(sessionElapsedStr));

        // Parse timestamp - handle both 24-hour and shorter races
        String timestampStr = lapJson.get("hour").getAsString();
        LocalDateTime timestamp = parseTimestamp(timestampStr, sessionDurationSeconds, sessionStartDateTime);
        newLap.setTimestamp(timestamp);

        // Set average speed if available
        if (lapJson.has("average_speed_kph") && !lapJson.get("average_speed_kph").isJsonNull()) {
            newLap.setAverageSpeedKph(new BigDecimal(lapJson.get("average_speed_kph").getAsString()));
        }

        // Set validity flags
        newLap.setIsValid(lapJson.get("is_valid").getAsBoolean());
        newLap.setIsPersonalBest(lapJson.get("is_personal_best").getAsBoolean());
        newLap.setIsSessionBest(lapJson.get("is_session_best").getAsBoolean());

        return lapRepository.save(newLap);
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
                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
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

    /**
     * Processes the sector times data.
     *
     * @param sectorTimesJson the JSON data for the sector times
     * @param lapId           the ID of the lap
     * @return the list of created sectors
     */
    private List<Sector> processSectorTimes(JsonArray sectorTimesJson, Long lapId) {
        List<Sector> sectors = new ArrayList<>();
        for (JsonElement sectorElement : sectorTimesJson) {
            JsonObject sectorJson = sectorElement.getAsJsonObject();

            // Create sector
            Sector sector = createSector(sectorJson, lapId);
            sectors.add(sector);
        }
        return sectors;
    }

    /**
     * Creates a sector with the given data.
     *
     * @param sectorJson the JSON data for the sector
     * @param lapId      the ID of the lap
     * @return the sector entity
     */
    private Sector createSector(JsonObject sectorJson, Long lapId) {
        // Parse sector number safely, handling both string and integer formats
        int sectorNumber = parseJsonNumberAsInt(sectorJson.get("index"), "sector index");

        // Check if sector already exists for this lap and sector number
        Optional<Sector> existingSector = sectorRepository.findByLapIdAndSectorNumber(lapId, sectorNumber);
        if (existingSector.isPresent()) {
            return existingSector.get();
        }

        Sector newSector = new Sector();
        newSector.setLapId(lapId);
        newSector.setSectorNumber(sectorNumber);

        // Parse sector time
        String sectorTimeStr = sectorJson.get("time").getAsString();
        BigDecimal sectorTime = parseSectorTime(sectorTimeStr);
        newSector.setSectorTimeSeconds(sectorTime);

        // Set validity based on sector time
        boolean isValid = sectorTime != null;
        newSector.setIsValid(isValid);

        if (!isValid) {
            if (sectorTimeStr == null || sectorTimeStr.trim().isEmpty()) {
                newSector.setInvalidationReason("Missing sector time");
            } else if (sectorTimeStr.matches("^[0-9]{5,}.*")) {
                newSector.setInvalidationReason("Unreasonably large sector time");
            } else {
                newSector.setInvalidationReason("Invalid sector time format");
            }
        }

        // Set best flags
        newSector.setIsPersonalBest(sectorJson.get("is_personal_best").getAsBoolean());
        newSector.setIsSessionBest(sectorJson.get("is_session_best").getAsBoolean());

        return sectorRepository.save(newSector);
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

    /**
     * Validates that a car number is reasonable.
     *
     * @param carNumber the car number to validate
     * @throws IllegalArgumentException if the car number is not reasonable
     */
    private void validateCarNumber(String carNumber) {
        if (carNumber == null || carNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Car number cannot be null or empty");
        }

        // Remove leading zeros for validation
        String normalizedNumber = carNumber.replaceFirst("^0+", "");
        if (normalizedNumber.isEmpty()) {
            normalizedNumber = "0"; // Handle case where number is all zeros
        }

        try {
            int numberValue = Integer.parseInt(normalizedNumber);
            if (numberValue < 0 || numberValue > 999) {
                throw new IllegalArgumentException("Car number must be between 0 and 999, got: " + carNumber);
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Car number must be a valid number, got: " + carNumber);
        }
    }
}
