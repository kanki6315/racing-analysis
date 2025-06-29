package com.arjunakankipati.racingstatanalysis.service.impl;

import com.arjunakankipati.racingstatanalysis.dto.ProcessRequestDTO;
import com.arjunakankipati.racingstatanalysis.dto.ProcessResponseDTO;
import com.arjunakankipati.racingstatanalysis.model.*;
import com.arjunakankipati.racingstatanalysis.model.Class;
import com.arjunakankipati.racingstatanalysis.repository.*;
import com.arjunakankipati.racingstatanalysis.service.ImportJobService;
import com.arjunakankipati.racingstatanalysis.service.ImportService;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of the ImportService interface.
 */
@Service
public class ImportServiceImpl implements ImportService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImportServiceImpl.class);
    private final EventRepository eventRepository;
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
    private final ResultRepository resultRepository;

    private final OkHttpClient httpClient;

    private final Cache<String, Team> teamCache = CacheBuilder.newBuilder().expireAfterWrite(20, TimeUnit.MINUTES).build();
    private final Cache<String, Class> classCache = CacheBuilder.newBuilder().expireAfterWrite(20, TimeUnit.MINUTES).build();
    private final Cache<String, CarModel> carModelCache = CacheBuilder.newBuilder().expireAfterWrite(20, TimeUnit.MINUTES).build();

    /**
     * Constructor with repository dependency injection.
     */
    @Autowired
    public ImportServiceImpl(EventRepository eventRepository,
                             SessionRepository sessionRepository,
                             TeamRepository teamRepository,
                             ClassRepository classRepository,
                             DriverRepository driverRepository,
                             CarEntryRepository carEntryRepository,
                             CarModelRepository carModelRepository,
                             CarDriverRepository carDriverRepository,
                             LapRepository lapRepository,
                             SectorRepository sectorRepository,
                             ImportJobService importJobService,
                             ResultRepository resultRepository) {
        this.eventRepository = eventRepository;
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
        this.resultRepository = resultRepository;

        // Initialize OkHttpClient with reasonable timeouts
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();
    }

    @Async
    @Override
    public void processImport(Integer jobId, ProcessRequestDTO request) {
        importJobService.markStarted(jobId);
        try {
            ProcessResponseDTO response;
            if (request.getProcessType() == ProcessRequestDTO.ProcessType.RESULTS) {
                response = processResultsCsv(request);
            } else {
                response = processTimecardCsv(request);
            }
            if (!response.getStatus().equals("SUCCESS")) {
                throw new RuntimeException("Failed to process import for URL: " + request.getUrl() + ". Error: " + response.getError());
            }
            importJobService.markCompleted(jobId);
        } catch (Exception e) {
            LOGGER.error("Failed to process import for URL: " + request.getUrl(), e);
            importJobService.markFailed(jobId, e.getMessage());
        }
    }

    @Override
    public ProcessResponseDTO processResultsCsv(ProcessRequestDTO request) {
        var importType = request.getImportType();
        try {
            // 1. Download the CSV file using OkHttp
            Request httpRequest = new Request.Builder()
                    .url(request.getUrl())
                    .build();
            try (Response response = httpClient.newCall(httpRequest).execute()) {
                if (!response.isSuccessful() || response.body() == null) {
                    return new ProcessResponseDTO(null, "FAILED", "Failed to fetch CSV: HTTP " + (response != null ? response.code() : "null response"));
                }
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.body().byteStream(), StandardCharsets.UTF_8));

                // 2. Parse the CSV header
                String headerLine = reader.readLine();
                if (headerLine == null) {
                    return new ProcessResponseDTO(null, "FAILED", "CSV is empty");
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
                List<Result> resultsToSave = new ArrayList<>();
                while ((row = reader.readLine()) != null) {
                    if (row.trim().isEmpty()) continue;
                    String[] values = row.split(";");

                    // --- Parse team, class, car model, car number, tire supplier ---
                    String teamName = getValueByHeader(headers, values, "TEAM");
                    String className = getValueByHeader(headers, values, "CLASS");
                    String carModelName = getValueByHeader(headers, values, "VEHICLE");
                    String carNumber = getValueByHeader(headers, values, "NUMBER");
                    String tireSupplier = getValueByHeader(headers, values,
                            importType.equals(ProcessRequestDTO.ImportType.WEC) ? "TYRES" : "TIRES");

                    // --- Find or create team, class, car model ---
                    Team team = findOrCreateTeam(teamName);
                    Class carClass = findOrCreateClass(event.getSeriesId(), className);
                    CarModel carModel = findOrCreateCarModel(carModelName);

                    // --- Create car entry ---
                    CarEntry carEntry = findOrCreateCarEntry(session.getId(), team.getId(), carClass.getId(), carModel.getId(), carNumber, tireSupplier);

                    // --- Parse and create drivers ---
                    for (int i = 1; i <= 6; i++) { // Support up to 6 drivers (IMSA)
                        String[] names;
                        if (importType == ProcessRequestDTO.ImportType.WEC) {
                            String driverCol = "DRIVER_" + i;
                            String driverName = getValueByHeader(headers, values, driverCol);
                            if (driverName == null || driverName.isBlank()) continue;
                            names = parseWECName(driverName);
                        } else if (importType == ProcessRequestDTO.ImportType.IMSA) { // IMSA splits up names into two columns
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

                    // --- Parse and save result row ---
                    Result result = new Result();
                    result.setSessionId(session.getId());
                    result.setCarEntryId(carEntry.getId());
                    result.setCarNumber(carNumber);
                    result.setTires(tireSupplier);
                    result.setStatus(getValueByHeader(headers, values, "STATUS"));
                    result.setLaps(parseInteger(getValueByHeader(headers, values, "LAPS")));
                    result.setTotalTime(getValueByHeader(headers, values, "TOTAL_TIME"));
                    result.setGapFirst(getValueByHeader(headers, values, "GAP_FIRST"));
                    result.setGapPrevious(getValueByHeader(headers, values, "GAP_PREVIOUS"));
                    result.setFlLapnum(parseInteger(getValueByHeader(headers, values, "FL_LAPNUM")));
                    result.setFlTime(getValueByHeader(headers, values, "FL_TIME"));
                    result.setFlKph(parseBigDecimal(getValueByHeader(headers, values, "FL_KPH")));
                    // csv files are prone to having BOM and java decided NOPE LETS NOT FIX
                    // https://bugs.java.com/bugdatabase/view_bug.do?bug_id=4508058
                    // Position is guaranteed to be position 0 in csvs, so we hardcode.
                    result.setPosition(parseInteger(values[0]));
                    resultsToSave.add(result);
                }
                // Save all results (batch)
                resultRepository.batchSave(resultsToSave);
                reader.close();
                return new ProcessResponseDTO(session.getId(), "SUCCESS", null);
            }
        } catch (Exception e) {
            LOGGER.error("Failed to process results CSV", e);
            return new ProcessResponseDTO(null, "FAILED", e.getMessage());
        }
    }

    @Override
    public ProcessResponseDTO processTimecardCsv(ProcessRequestDTO request) {
        try {
            // 1. Download the CSV file using OkHttp
            Request httpRequest = new Request.Builder()
                    .url(request.getUrl())
                    .build();
            try (Response response = httpClient.newCall(httpRequest).execute()) {
                if (!response.isSuccessful() || response.body() == null) {
                    return new ProcessResponseDTO(null, "FAILED", "Failed to fetch CSV: HTTP " + (response != null ? response.code() : "null response"));
                }
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.body().byteStream(), StandardCharsets.UTF_8));

                // 2. Parse the CSV header
                String headerLine = reader.readLine();
                if (headerLine == null) {
                    return new ProcessResponseDTO(null, "FAILED", "CSV is empty");
                }
                String[] headers = headerLine.split(";");

                // 3. Ensure session exists
                Session session;
                if (request.getSessionId() != null) {
                    session = sessionRepository.findById(request.getSessionId())
                            .orElseThrow(() -> new IllegalArgumentException("Session not found: " + request.getSessionId()));
                } else {
                    throw new IllegalArgumentException("sessionId is required in the request");
                }

                // 4. Process each row, collect laps and sector data using a map
                String row;
                var carEntries = carEntryRepository.findBySessionId(session.getId());

                // Use a map for laps and their sectors
                var lapMap = new HashMap<LapKey, Lap>();
                var sectorMap = new HashMap<LapKey, List<Sector>>();

                while ((row = reader.readLine()) != null) {
                    if (row.trim().isEmpty()) continue;
                    String[] values = row.split(";");

                    // --- Parse car number and driver name/number ---
                    String carNumber = values[0];
                    Integer driverNumber = parseInteger(getValueByHeader(headers, values, "DRIVER_NUMBER"));
                    String driverName = getValueByHeader(headers, values, "DRIVER_NAME");

                    // --- Look up car entry ---
                    CarEntry carEntry = carEntries.stream().filter(e -> e.getNumber().equals(carNumber)).findFirst()
                            .orElseThrow(() -> new IllegalArgumentException("Car entry not found for session " + session.getId() + " and car number " + carNumber));

                    // --- Look up car-driver association ---
                    CarDriver carDriver = null;
                    if (driverNumber != null) {
                        carDriver = carDriverRepository.findByCarIdAndDriverNumber(carEntry.getId(), driverNumber)
                                .orElseThrow(() -> new IllegalArgumentException("Car-driver association not found for car entry " + carEntry.getId() + " and driver number " + driverNumber));
                    } else if (driverName != null && !driverName.isBlank()) {
                        var driver = driverRepository.findByName(driverName);
                        if (driver.isPresent()) {
                            var optCD = carDriverRepository.findByCarIdAndDriverId(carEntry.getId(), driver.get().getId());
                            if (optCD.isPresent()) {
                                carDriver = optCD.get();
                            }
                        }
                        if (carDriver == null) {
                            throw new IllegalArgumentException("Car-driver association not found for car entry " + carEntry.getId() + " and driver name " + driverName);
                        }
                    } else {
                        throw new IllegalArgumentException("Driver number or name must be present in timecard row");
                    }

                    Integer lapNumber = parseInteger(getValueByHeader(headers, values, "LAP_NUMBER"));
                    LapKey lapKey = new LapKey(carEntry.getId(), carDriver.getDriverId(), lapNumber);

                    // --- Parse lap data ---
                    Lap lap = new Lap();
                    lap.setCarEntryId(carEntry.getId());
                    lap.setDriverId(carDriver.getDriverId());
                    lap.setLapNumber(lapNumber);
                    lap.setLapTimeSeconds(parseLapTime(getValueByHeader(headers, values, "LAP_TIME")));

                    var seconds = parseTimestampIntoSeconds(getValueByHeader(headers, values, "ELAPSED"));
                    lap.setSessionElapsedSeconds(seconds);
                    lap.setTimestamp(parseTimestamp(getValueByHeader(headers, values, "HOUR"), seconds, session.getStartDatetime()));
                    lap.setAverageSpeedKph(parseBigDecimal(getValueByHeader(headers, values, "KPH")));
                    // TODO: Parse PIT_TIME (not currently in Lap model)
                    // TODO: Parse FLAG_AT_FL (not currently in Lap model)
                    lapMap.put(lapKey, lap);

                    // --- Parse sector data ---
                    List<Sector> sectorsForLap = new java.util.ArrayList<>();
                    for (int i = 1; i <= 3; i++) {
                        String sectorTime = getValueByHeader(headers, values, String.format("S%d_LARGE", i));
                        if (sectorTime != null && !sectorTime.isBlank()) {
                            Sector sector = new Sector();
                            // lapId will be set after batch save
                            sector.setSectorNumber(i);
                            sector.setSectorTimeSeconds(parseLargeSectorTime(sectorTime));
                            if (sector.getSectorTimeSeconds() == null) {
                                throw new IllegalArgumentException("Invalid sector time: " + sectorTime);
                            }
                            // TODO: Parse S{i}_IMPROVEMENT, S{i}_LARGE (not currently in Sector model)
                            sectorsForLap.add(sector);
                        }
                    }
                    sectorMap.put(lapKey, sectorsForLap);
                }
                // Batch save all laps
                List<Lap> savedLaps = lapRepository.saveAll(new ArrayList<>(lapMap.values()));
                // Build a map from LapKey to saved Lap (with ID)
                var savedLapMap = new java.util.HashMap<LapKey, Lap>();
                int idx = 0;
                for (LapKey key : lapMap.keySet()) {
                    savedLapMap.put(key, savedLaps.get(idx++));
                }
                // Now assign lapIds to sectors and collect all sectors
                var allSectors = new java.util.ArrayList<Sector>();
                for (var entry : sectorMap.entrySet()) {
                    LapKey key = entry.getKey();
                    Lap savedLap = savedLapMap.get(key);
                    for (Sector sector : entry.getValue()) {
                        sector.setLapId(savedLap.getId());
                        allSectors.add(sector);
                    }
                }
                // Batch save all sectors
                sectorRepository.saveAll(allSectors);
                reader.close();
                return new ProcessResponseDTO(session.getId(), "SUCCESS", null);
            }
        } catch (Exception e) {
            LOGGER.error("Failed to process timecard CSV", e);
            return new ProcessResponseDTO(null, "FAILED", e.getMessage());
        }
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

    /**
     * Finds or creates a car model with the given data.
     *
     * @param vehicleModel the vehicle model name
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
     * Parses a lap time string (e.g., "1:48.656") to seconds.
     *
     * @param lapTimeStr the lap time string
     * @return the lap time in seconds as a BigDecimal
     */
    private BigDecimal parseLapTime(String lapTimeStr) {
        String[] parts = lapTimeStr.split(":");
        if (parts.length == 3) {
            int hours = Integer.parseInt(parts[0]);
            int minutes = Integer.parseInt(parts[1]);
            double seconds = Double.parseDouble(parts[2]);

            return BigDecimal.valueOf(hours * 60 * 60 + minutes * 60 + seconds);
        } else if (parts.length == 2) {
            int minutes = Integer.parseInt(parts[0]);
            double seconds = Double.parseDouble(parts[1]);

            return BigDecimal.valueOf(minutes * 60 + seconds);
        }
        throw new IllegalArgumentException("Invalid lap time format: " + lapTimeStr);
    }

    private BigDecimal parseLargeSectorTime(String sectorTime) {
        // Format: MM:SS.SSS
        String[] parts = sectorTime.split(":");
        int minutes = Integer.parseInt(parts[0]);
        double seconds = Double.parseDouble(parts[1]);
        double totalSeconds = minutes * 60 + seconds;
        return BigDecimal.valueOf(totalSeconds);
    }

    private BigDecimal parseTimestampIntoSeconds(String timestampStr) {
        if (timestampStr == null || timestampStr.isBlank()) {
            throw new IllegalArgumentException("Timestamp cannot be null or empty");
        }

        try {
            // Split the timestamp by colons to determine format
            String[] parts = timestampStr.split(":");

            if (parts.length == 2) {
                // Format: m:ss.SSS or mm:ss.SSS (e.g., 6:57.136 or 15:24.428)
                int minutes = Integer.parseInt(parts[0]);

                // Parse seconds and milliseconds
                String[] secondParts = parts[1].split("\\.");
                int seconds = Integer.parseInt(secondParts[0]);
                int milliseconds = secondParts.length > 1 ? Integer.parseInt(secondParts[1]) : 0;

                // Calculate total seconds as BigDecimal
                BigDecimal totalSeconds = BigDecimal.valueOf(minutes * 60 + seconds);
                BigDecimal fraction = BigDecimal.valueOf(milliseconds).divide(BigDecimal.valueOf(1000), 6, BigDecimal.ROUND_HALF_UP);

                return totalSeconds.add(fraction);
            } else if (parts.length == 3) {
                // Format: h:mm:ss.SSS or hh:mm:ss.SSS (e.g., 4:30:00.623 or 11:14:52.128 or 13:42:36.003)
                int hours = Integer.parseInt(parts[0]);
                int minutes = Integer.parseInt(parts[1]);

                // Parse seconds and milliseconds
                String[] secondParts = parts[2].split("\\.");
                int seconds = Integer.parseInt(secondParts[0]);
                int milliseconds = secondParts.length > 1 ? Integer.parseInt(secondParts[1]) : 0;

                // Calculate total seconds as BigDecimal
                BigDecimal totalSeconds = BigDecimal.valueOf(hours * 3600 + minutes * 60 + seconds);
                BigDecimal fraction = BigDecimal.valueOf(milliseconds).divide(BigDecimal.valueOf(1000), 6, BigDecimal.ROUND_HALF_UP);

                return totalSeconds.add(fraction);
            } else {
                LOGGER.error("Unrecognized timestamp format: {}", timestampStr);
                throw new IllegalArgumentException("Invalid timestamp format: " + timestampStr);
            }
        } catch (Exception ex) {
            LOGGER.error("Failed to parse timestamp: {} - {}", timestampStr, ex.getMessage());
            throw new IllegalArgumentException("Invalid timestamp format: " + timestampStr);
        }
    }

    private LocalDateTime parseTimestamp(String timestampStr, BigDecimal elapsedTime, LocalDateTime sessionStartDateTime) {
        if (timestampStr == null || timestampStr.trim().isEmpty()) {
            throw new IllegalArgumentException("Timestamp cannot be null or empty");
        }

        // Clean the timestamp string
        String cleanTimestampStr = timestampStr.trim();

        try {
            // Split the timestamp by colons and period
            String[] parts = cleanTimestampStr.split("[:.]");

            if (parts.length != 3 && parts.length != 4) {
                throw new IllegalArgumentException("Invalid timestamp format: " + cleanTimestampStr);
            }

            int hours = 0;
            int minutes = 0;
            int seconds = 0;
            int millis = 0;

            if (parts.length == 3) {
                // Format: mm:ss.SSS
                minutes = Integer.parseInt(parts[0]);
                seconds = Integer.parseInt(parts[1]);
                millis = Integer.parseInt(parts[2]);
            } else if (parts.length == 4) {
                // Format: HH:mm:ss.SSS
                hours = Integer.parseInt(parts[0]);
                minutes = Integer.parseInt(parts[1]);
                seconds = Integer.parseInt(parts[2]);
                millis = Integer.parseInt(parts[3]);
            }

            // Create LocalTime
            LocalTime time = LocalTime.of(hours, minutes, seconds, millis * 1_000_000);

            // Calculate the date by adding elapsed time to session start
            LocalDateTime lapSetAt = sessionStartDateTime.plusSeconds(elapsedTime.longValue());

            // Combine the date from lapSetAt with the parsed time
            return LocalDateTime.of(lapSetAt.toLocalDate(), time);
        } catch (NumberFormatException e) {
            LOGGER.warn("Failed to parse timestamp '{}': Invalid number format", cleanTimestampStr);
            throw new IllegalArgumentException("Invalid number in timestamp: " + cleanTimestampStr, e);
        } catch (Exception e) {
            LOGGER.warn("Failed to parse timestamp '{}': {}", cleanTimestampStr, e.getMessage());
            throw new IllegalArgumentException("Error parsing timestamp: " + cleanTimestampStr, e);
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

    private Integer parseInteger(String value) {
        try {
            if (value == null || value.isBlank()) return null;
            return Integer.parseInt(value.trim());
        } catch (Exception e) {
            return null;
        }
    }

    private BigDecimal parseBigDecimal(String value) {
        try {
            if (value == null || value.isBlank()) return null;
            return new BigDecimal(value.trim());
        } catch (Exception e) {
            return null;
        }
    }

    // Helper class for robust lap mapping
    private static class LapKey {
        private final Long carId;
        private final Long driverId;
        private final Integer lapNumber;

        public LapKey(Long carId, Long driverId, Integer lapNumber) {
            this.carId = carId;
            this.driverId = driverId;
            this.lapNumber = lapNumber;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            LapKey lapKey = (LapKey) o;
            return Objects.equals(carId, lapKey.carId) &&
                    Objects.equals(driverId, lapKey.driverId) &&
                    Objects.equals(lapNumber, lapKey.lapNumber);
        }

        @Override
        public int hashCode() {
            return Objects.hash(carId, driverId, lapNumber);
        }
    }
}
