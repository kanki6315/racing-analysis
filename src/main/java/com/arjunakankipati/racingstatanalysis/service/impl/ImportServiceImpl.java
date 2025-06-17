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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of the ImportService interface.
 */
@Service
public class ImportServiceImpl implements ImportService {

    private final SeriesRepository seriesRepository;
    private final EventRepository eventRepository;
    private final CircuitRepository circuitRepository;
    private final SessionRepository sessionRepository;
    private final TeamRepository teamRepository;
    private final ManufacturerRepository manufacturerRepository;
    private final ClassRepository classRepository;
    private final DriverRepository driverRepository;
    private final CarRepository carRepository;
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
                             CarRepository carRepository,
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
        this.carRepository = carRepository;
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
        try {
            // Validate request
            if (request == null) {
                throw new IllegalArgumentException("Import request cannot be null");
            }
            if (request.getUrl() == null || request.getUrl().trim().isEmpty()) {
                throw new IllegalArgumentException("URL cannot be null or empty");
            }
            if (request.getSeriesName() == null || request.getSeriesName().trim().isEmpty()) {
                throw new IllegalArgumentException("Series name cannot be null or empty");
            }
            if (request.getEventName() == null || request.getEventName().trim().isEmpty()) {
                throw new IllegalArgumentException("Event name cannot be null or empty");
            }
            if (request.getYear() == null) {
                throw new IllegalArgumentException("Year cannot be null");
            }

            // Generate a unique import ID
            String importId = UUID.randomUUID().toString();

            // Fetch JSON data from URL
            JsonObject jsonData = fetchJsonData(request.getUrl());

            // Validate JSON structure
            validateJsonStructure(jsonData);

            // Process and store the data
            processData(jsonData, request);

            // Return response
            return new ImportResponseDTO(
                    importId,
                    "completed",
                    LocalDateTime.now()
            );
        } catch (Exception e) {
            // Log the error
            e.printStackTrace();

            // Return error response
            return new ImportResponseDTO(
                    UUID.randomUUID().toString(),
                    "failed: " + e.getMessage(),
                    LocalDateTime.now()
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
    }

    /**
     * Processes and stores the data from the JSON.
     *
     * @param jsonData the JSON data to process
     * @param request  the import request containing metadata
     */
    private void processData(JsonObject jsonData, ImportRequestDTO request) {
        // Extract session data
        JsonObject sessionJson = jsonData.getAsJsonObject("session");
        JsonObject circuitJson = sessionJson.getAsJsonObject("circuit");

        // Find or create series
        Series series = findOrCreateSeries(request.getSeriesName());

        // Find or create circuit
        Circuit circuit = findOrCreateCircuit(circuitJson);

        // Find or create event
        Event event = findOrCreateEvent(request.getEventName(), request.getYear(), series.getId());

        // Create session
        Session session = createSession(sessionJson, event.getId(), circuit.getId(), request.getUrl());

        // Process participants
        JsonArray participantsJson = jsonData.getAsJsonArray("participants");
        processParticipants(participantsJson, session.getId());
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
        Optional<Event> existingEvent = eventRepository.findByNameAndYear(eventName, year);
        if (existingEvent.isPresent()) {
            return existingEvent.get();
        }

        Event newEvent = new Event();
        newEvent.setName(eventName);
        newEvent.setYear(year);
        newEvent.setSeriesId(seriesId);
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

        // Set duration (default to 24 hours for endurance races)
        newSession.setDurationSeconds(86400);

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

        return sessionRepository.save(newSession);
    }

    /**
     * Processes the participants data.
     *
     * @param participantsJson the JSON data for the participants
     * @param sessionId        the ID of the session
     */
    private void processParticipants(JsonArray participantsJson, Long sessionId) {
        for (JsonElement participantElement : participantsJson) {
            JsonObject participantJson = participantElement.getAsJsonObject();

            // Find or create team
            Team team = findOrCreateTeam(participantJson.get("team").getAsString());

            // Find or create manufacturer
            Manufacturer manufacturer = findOrCreateManufacturer(
                    participantJson.get("manufacturer").getAsString());

            // Find or create class
            Class carClass = findOrCreateClass(participantJson.get("class").getAsString());

            // Create car
            Car car = createCar(participantJson, sessionId, team.getId(),
                    carClass.getId(), manufacturer.getId());

            // Process drivers
            if (participantJson.has("drivers") && participantJson.get("drivers").isJsonArray()) {
                JsonArray driversJson = participantJson.getAsJsonArray("drivers");
                processDrivers(driversJson, car.getId());
            }

            // Process laps
            if (participantJson.has("laps") && participantJson.get("laps").isJsonArray()) {
                JsonArray lapsJson = participantJson.getAsJsonArray("laps");
                processLaps(lapsJson, car.getId());
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
    private Class findOrCreateClass(String className) {
        Optional<Class> existingClass = classRepository.findByName(className);
        if (existingClass.isPresent()) {
            return existingClass.get();
        }

        Class newClass = new Class();
        newClass.setName(className);
        return classRepository.save(newClass);
    }

    /**
     * Creates a car with the given data.
     *
     * @param participantJson the JSON data for the participant
     * @param sessionId       the ID of the session
     * @param teamId          the ID of the team
     * @param classId         the ID of the class
     * @param manufacturerId  the ID of the manufacturer
     * @return the car entity
     */
    private Car createCar(JsonObject participantJson, Long sessionId, Long teamId,
                          Long classId, Long manufacturerId) {
        String carNumber = participantJson.get("number").getAsString();

        // Check if car already exists for this session and number
        Optional<Car> existingCar = carRepository.findBySessionIdAndNumber(sessionId, carNumber);
        if (existingCar.isPresent()) {
            return existingCar.get();
        }

        Car newCar = new Car();
        newCar.setSessionId(sessionId);
        newCar.setTeamId(teamId);
        newCar.setClassId(classId);
        newCar.setManufacturerId(manufacturerId);
        newCar.setNumber(carNumber);
        newCar.setModel(participantJson.get("vehicle").getAsString());

        if (participantJson.has("tires") && !participantJson.get("tires").isJsonNull()) {
            newCar.setTireSupplier(participantJson.get("tires").getAsString());
        }

        return carRepository.save(newCar);
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

            // Create car-driver association
            createCarDriver(carId, driver.getId(), driverJson.get("number").getAsInt());
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
     */
    private void processLaps(JsonArray lapsJson, Long carId) {
        for (JsonElement lapElement : lapsJson) {
            JsonObject lapJson = lapElement.getAsJsonObject();

            // Get driver number
            int driverNumber = Integer.parseInt(lapJson.get("driver_number").getAsString());

            // Find driver ID for this car and driver number
            Optional<CarDriver> carDriver = carDriverRepository.findByCarIdAndDriverNumber(carId, driverNumber);
            if (!carDriver.isPresent()) {
                // Skip this lap if driver not found
                continue;
            }

            // Create lap
            Lap lap = createLap(lapJson, carId, carDriver.get().getDriverId());

            // Process sector times
            if (lapJson.has("sector_times") && lapJson.get("sector_times").isJsonArray()) {
                JsonArray sectorTimesJson = lapJson.getAsJsonArray("sector_times");
                processSectorTimes(sectorTimesJson, lap.getId());
            }
        }
    }

    /**
     * Creates a lap with the given data.
     *
     * @param lapJson  the JSON data for the lap
     * @param carId    the ID of the car
     * @param driverId the ID of the driver
     * @return the lap entity
     */
    private Lap createLap(JsonObject lapJson, Long carId, Long driverId) {
        int lapNumber = lapJson.get("number").getAsInt();

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

        // Parse timestamp
        String timestampStr = lapJson.get("hour").getAsString();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy h:mm:ss a");
        newLap.setTimestamp(LocalDateTime.parse(timestampStr, formatter));

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
     * Processes the sector times data.
     *
     * @param sectorTimesJson the JSON data for the sector times
     * @param lapId           the ID of the lap
     */
    private void processSectorTimes(JsonArray sectorTimesJson, Long lapId) {
        for (JsonElement sectorElement : sectorTimesJson) {
            JsonObject sectorJson = sectorElement.getAsJsonObject();

            // Create sector
            createSector(sectorJson, lapId);
        }
    }

    /**
     * Creates a sector with the given data.
     *
     * @param sectorJson the JSON data for the sector
     * @param lapId      the ID of the lap
     * @return the sector entity
     */
    private Sector createSector(JsonObject sectorJson, Long lapId) {
        int sectorNumber = sectorJson.get("index").getAsInt();

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
        newSector.setSectorTimeSeconds(new BigDecimal(sectorTimeStr));

        // Set best flags
        newSector.setIsPersonalBest(sectorJson.get("is_personal_best").getAsBoolean());
        newSector.setIsSessionBest(sectorJson.get("is_session_best").getAsBoolean());

        return sectorRepository.save(newSector);
    }
}
