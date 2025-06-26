package com.arjunakankipati.racingstatanalysis.repository.impl;

import com.arjunakankipati.racingstatanalysis.dto.DriverLapTimeAnalysisDTO;
import com.arjunakankipati.racingstatanalysis.dto.DriverLapTimesDTO;
import com.arjunakankipati.racingstatanalysis.dto.LapTimeAnalysisDTO;
import com.arjunakankipati.racingstatanalysis.dto.LapTimeDetailDTO;
import com.arjunakankipati.racingstatanalysis.jooq.Tables;
import com.arjunakankipati.racingstatanalysis.model.Lap;
import com.arjunakankipati.racingstatanalysis.repository.LapRepository;
import org.jooq.*;
import org.jooq.Record;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.*;


/**
 * Implementation of the LapRepository interface using JOOQ.
 * Extends BaseRepositoryImpl to inherit common CRUD operations.
 */
@Repository
public class LapRepositoryImpl extends BaseRepositoryImpl<Lap, Long> implements LapRepository {

    /**
     * Constructor with DSLContext dependency injection.
     *
     * @param dsl the JOOQ DSL context
     */
    @Autowired
    public LapRepositoryImpl(DSLContext dsl) {
        super(dsl, Tables.LAPS, Tables.LAPS.ID);
    }

    @Override
    protected Lap mapToEntity(Record record) {
        if (record == null) {
            return null;
        }
        var lapRec = record.into(Tables.LAPS);
        return new Lap(
                lapRec.getId(),
                lapRec.getCarId(),
                lapRec.getDriverId(),
                lapRec.getLapNumber(),
                lapRec.getLapTimeSeconds(),
                lapRec.getSessionElapsedSeconds(),
                lapRec.getTimestamp(),
                lapRec.getAverageSpeedKph());
    }

    @Override
    protected Lap insert(Lap lap) {
        Record record = dsl.insertInto(table)
                .columns(
                        Tables.LAPS.CAR_ID,
                        Tables.LAPS.DRIVER_ID,
                        Tables.LAPS.LAP_NUMBER,
                        Tables.LAPS.LAP_TIME_SECONDS,
                        Tables.LAPS.SESSION_ELAPSED_SECONDS,
                        Tables.LAPS.TIMESTAMP,
                        Tables.LAPS.AVERAGE_SPEED_KPH
                )
                .values(
                        lap.getCarId(),
                        lap.getDriverId(),
                        lap.getLapNumber(),
                        lap.getLapTimeSeconds(),
                        lap.getSessionElapsedSeconds(),
                        lap.getTimestamp(),
                        lap.getAverageSpeedKph()
                )
                .returning()
                .fetchOne();

        return mapToEntity(record);
    }

    @Override
    protected void update(Lap lap) {
        dsl.update(table)
                .set(Tables.LAPS.CAR_ID, lap.getCarId())
                .set(Tables.LAPS.DRIVER_ID, lap.getDriverId())
                .set(Tables.LAPS.LAP_NUMBER, lap.getLapNumber())
                .set(Tables.LAPS.LAP_TIME_SECONDS, lap.getLapTimeSeconds())
                .set(Tables.LAPS.SESSION_ELAPSED_SECONDS, lap.getSessionElapsedSeconds())
                .set(Tables.LAPS.TIMESTAMP, lap.getTimestamp())
                .set(Tables.LAPS.AVERAGE_SPEED_KPH, lap.getAverageSpeedKph())
                .where(idField.eq(lap.getId()))
                .execute();
    }

    @Override
    public List<Lap> findTopPercentageLapsByDriverId(Long driverId, boolean isValid, int percentage,
                                                     Optional<Long> sessionId,
                                                     Optional<Long> eventId,
                                                     Optional<Integer> year,
                                                     Optional<Long> seriesId) {
        // Build the query with joins
        var query = dsl.select()
                .from(table)
                .join(Tables.CAR_ENTRIES).on(Tables.CAR_ENTRIES.ID.eq(Tables.LAPS.CAR_ID))
                .join(Tables.SESSIONS).on(Tables.SESSIONS.ID.eq(Tables.CAR_ENTRIES.SESSION_ID))
                .join(Tables.EVENTS).on(Tables.EVENTS.ID.eq(Tables.SESSIONS.EVENT_ID));

        // Start with base conditions
        var whereCondition = Tables.LAPS.DRIVER_ID.eq(driverId);

        // Add optional filters
        if (sessionId.isPresent()) {
            whereCondition = whereCondition.and(Tables.CAR_ENTRIES.SESSION_ID.eq(sessionId.get()));
        }

        if (eventId.isPresent()) {
            whereCondition = whereCondition.and(Tables.SESSIONS.EVENT_ID.eq(eventId.get()));
        }

        if (year.isPresent()) {
            whereCondition = whereCondition.and(Tables.EVENTS.YEAR.eq(year.get()));
        }

        if (seriesId.isPresent()) {
            whereCondition = whereCondition.and(Tables.EVENTS.SERIES_ID.eq(seriesId.get()));
        }

        // First, get the total count of laps that match the criteria
        long totalLaps = dsl.selectCount()
                .from(table)
                .join(Tables.CAR_ENTRIES).on(Tables.CAR_ENTRIES.ID.eq(Tables.LAPS.CAR_ID))
                .join(Tables.SESSIONS).on(Tables.SESSIONS.ID.eq(Tables.CAR_ENTRIES.SESSION_ID))
                .join(Tables.EVENTS).on(Tables.EVENTS.ID.eq(Tables.SESSIONS.EVENT_ID))
                .where(whereCondition)
                .fetchOne(0, Long.class);

        // Calculate how many laps to return based on percentage
        int topCount = Math.max(1, (int) Math.ceil(totalLaps * percentage / 100.0));

        // Execute the query with the where condition, order by lap time, and limit to top percentage
        return query.where(whereCondition)
                .orderBy(Tables.LAPS.LAP_TIME_SECONDS.asc())
                .limit(topCount)
                .fetch()
                .map(this::mapToEntity);
    }

    @Override
    public LapTimeAnalysisDTO calculateLapTimeAnalysisForEvent(
            Long eventId,
            int percentage,
            Optional<Long> classId,
            Optional<Long> carId,
            Optional<Long> sessionId,
            Optional<Integer> offset,
            Optional<Integer> limit) {

        // Start with base conditions for valid laps in the specified event
        var whereCondition = Tables.SESSIONS.EVENT_ID.eq(eventId);

        // Add optional filters
        if (classId.isPresent()) {
            whereCondition = whereCondition.and(Tables.CAR_ENTRIES.CLASS_ID.eq(classId.get()));
        }

        if (carId.isPresent()) {
            whereCondition = whereCondition.and(Tables.CAR_ENTRIES.CAR_MODEL_ID.eq(carId.get()));
        }

        if (sessionId.isPresent()) {
            whereCondition = whereCondition.and(Tables.CAR_ENTRIES.SESSION_ID.eq(sessionId.get()));
        }

        // First, get the total count of laps that match the criteria
        Integer totalLapCount = dsl.selectCount()
                .from(table)
                .join(Tables.CAR_ENTRIES).on(Tables.CAR_ENTRIES.ID.eq(Tables.LAPS.CAR_ID))
                .join(Tables.SESSIONS).on(Tables.SESSIONS.ID.eq(Tables.CAR_ENTRIES.SESSION_ID))
                .where(whereCondition)
                .fetchOne(0, Integer.class);

        if (totalLapCount == null || totalLapCount == 0) {
            // Return empty analysis if no laps found
            return new LapTimeAnalysisDTO("0:00.000", "0:00.000", "0:00.000", 0, eventId);
        }

        // Calculate how many laps to include in the average based on percentage
        int topCount = Math.max(1, (int) Math.ceil(totalLapCount * percentage / 100.0));

        // Apply pagination if specified
        Integer finalOffset = offset.orElse(0);
        Integer finalLimit = limit.orElse(totalLapCount);

        // Calculate statistics using SQL
        // We need to calculate:
        // 1. Average of top percentage of lap times
        // 2. Fastest lap time (minimum)
        // 3. Median lap time

        // Calculate fastest lap time (minimum)
        Field<BigDecimal> minField = DSL.min(Tables.LAPS.LAP_TIME_SECONDS).as("fastest_lap_time");

        // Calculate median lap time
        // Note: JOOQ doesn't have a direct median function, so we'll use percentile_cont(0.5)
        Field<BigDecimal> medianField = DSL.field(
                "percentile_cont(0.5) within group (order by {0})",
                BigDecimal.class,
                Tables.LAPS.LAP_TIME_SECONDS
        ).as("median_lap_time");

        // Calculate average of top percentage of lap times using a proper jOOQ subquery
        var topLapsSubquery = dsl.select(Tables.LAPS.LAP_TIME_SECONDS.as("lap_time"))
                .from(table)
                .join(Tables.CAR_ENTRIES).on(Tables.CAR_ENTRIES.ID.eq(Tables.LAPS.CAR_ID))
                .join(Tables.SESSIONS).on(Tables.SESSIONS.ID.eq(Tables.CAR_ENTRIES.SESSION_ID))
                .where(whereCondition)
                .orderBy(Tables.LAPS.LAP_TIME_SECONDS.asc())
                .limit(topCount);

        Field<BigDecimal> avgField = dsl.select(DSL.avg(DSL.field("lap_time", BigDecimal.class)))
                .from(topLapsSubquery)
                .asField("avg_lap_time");

        // Execute the query to get the statistics
        Record4<BigDecimal, BigDecimal, BigDecimal, Integer> result = dsl.select(
                        avgField,
                        minField,
                        medianField,
                        DSL.val(totalLapCount).as("total_lap_count")
                )
                .from(table)
                .join(Tables.CAR_ENTRIES).on(Tables.CAR_ENTRIES.ID.eq(Tables.LAPS.CAR_ID))
                .join(Tables.SESSIONS).on(Tables.SESSIONS.ID.eq(Tables.CAR_ENTRIES.SESSION_ID))
                .where(whereCondition)
                .offset(finalOffset)
                .limit(finalLimit)
                .fetchOne();

        if (result == null) {
            // Return empty analysis if no results
            return new LapTimeAnalysisDTO("0:00.000", "0:00.000", "0:00.000", 0, eventId);
        }

        // Format the lap times as "m:ss.SSS"
        String averageLapTime = formatLapTime(result.get(avgField));
        String fastestLapTime = formatLapTime(result.get(minField));
        String medianLapTime = formatLapTime(result.get(medianField));

        // Create and return the DTO
        return new LapTimeAnalysisDTO(
                averageLapTime,
                fastestLapTime,
                medianLapTime,
                totalLapCount,
                eventId
        );
    }

    @Override
    public List<DriverLapTimeAnalysisDTO> calculateLapTimeAnalysisPerDriverForEvent(
            Long eventId,
            int percentage,
            Optional<Long> classId,
            Optional<Long> carId,
            Optional<Long> sessionId,
            Optional<Integer> offset,
            Optional<Integer> limit) {

        // Start with base conditions for valid laps in the specified event
        var whereCondition = Tables.SESSIONS.EVENT_ID.eq(eventId);

        // Add optional filters
        if (classId.isPresent()) {
            whereCondition = whereCondition.and(Tables.CAR_ENTRIES.CLASS_ID.eq(classId.get()));
        }

        if (carId.isPresent()) {
            whereCondition = whereCondition.and(Tables.CAR_ENTRIES.CAR_MODEL_ID.eq(carId.get()));
        }

        if (sessionId.isPresent()) {
            whereCondition = whereCondition.and(Tables.CAR_ENTRIES.SESSION_ID.eq(sessionId.get()));
        }

        // Apply pagination if specified
        Integer finalOffset = offset.orElse(0);
        Integer finalLimit = limit.orElse(Integer.MAX_VALUE);

        // We need to join with additional tables to get driver, car, team, and class information
        SelectJoinStep<?> baseQuery = dsl.select()
                .from(table)
                .join(Tables.CAR_ENTRIES).on(Tables.CAR_ENTRIES.ID.eq(Tables.LAPS.CAR_ID))
                .join(Tables.SESSIONS).on(Tables.SESSIONS.ID.eq(Tables.CAR_ENTRIES.SESSION_ID))
                .join(Tables.DRIVERS).on(Tables.DRIVERS.ID.eq(Tables.LAPS.DRIVER_ID))
                .join(Tables.TEAMS).on(Tables.TEAMS.ID.eq(Tables.CAR_ENTRIES.TEAM_ID))
                .join(Tables.CLASSES).on(Tables.CLASSES.ID.eq(Tables.CAR_ENTRIES.CLASS_ID))
                .join(Tables.CAR_MODELS).on(Tables.CAR_MODELS.ID.eq(Tables.CAR_ENTRIES.CAR_MODEL_ID));

        // First, get all distinct drivers that match the criteria
        Result<?> driversResult = dsl.selectDistinct(
                        Tables.DRIVERS.ID,
                        Tables.DRIVERS.FIRST_NAME,
                        Tables.DRIVERS.LAST_NAME,
                        Tables.DRIVERS.NATIONALITY,
                        Tables.CAR_ENTRIES.ID.as("car_id"),
                        Tables.CAR_ENTRIES.NUMBER,
                        Tables.CAR_MODELS.NAME.as("car_model"),
                        Tables.TEAMS.ID.as("team_id"),
                        Tables.TEAMS.NAME.as("team_name"),
                        Tables.CLASSES.ID.as("class_id"),
                        Tables.CLASSES.NAME.as("class_name")
                )
                .from(table)
                .join(Tables.CAR_ENTRIES).on(Tables.CAR_ENTRIES.ID.eq(Tables.LAPS.CAR_ID))
                .join(Tables.SESSIONS).on(Tables.SESSIONS.ID.eq(Tables.CAR_ENTRIES.SESSION_ID))
                .join(Tables.DRIVERS).on(Tables.DRIVERS.ID.eq(Tables.LAPS.DRIVER_ID))
                .join(Tables.TEAMS).on(Tables.TEAMS.ID.eq(Tables.CAR_ENTRIES.TEAM_ID))
                .join(Tables.CLASSES).on(Tables.CLASSES.ID.eq(Tables.CAR_ENTRIES.CLASS_ID))
                .join(Tables.CAR_MODELS).on(Tables.CAR_MODELS.ID.eq(Tables.CAR_ENTRIES.CAR_MODEL_ID))
                .where(whereCondition)
                .orderBy(Tables.DRIVERS.LAST_NAME, Tables.DRIVERS.FIRST_NAME)
                .offset(finalOffset)
                .limit(finalLimit)
                .fetch();

        List<DriverLapTimeAnalysisDTO> driverAnalyses = new ArrayList<>();

        // For each driver, calculate lap time statistics
        for (Record driverRecord : driversResult) {
            Long driverId = driverRecord.get(Tables.DRIVERS.ID);
            String firstName = driverRecord.get(Tables.DRIVERS.FIRST_NAME);
            String lastName = driverRecord.get(Tables.DRIVERS.LAST_NAME);
            String driverName = firstName + " " + lastName;
            String nationality = driverRecord.get(Tables.DRIVERS.NATIONALITY);

            Long driverCarId = driverRecord.get("car_id", Long.class);
            String carNumber = driverRecord.get(Tables.CAR_ENTRIES.NUMBER);
            String carModel = driverRecord.get("car_model", String.class);

            Long teamId = driverRecord.get("team_id", Long.class);
            String teamName = driverRecord.get("team_name", String.class);

            Long driverClassId = driverRecord.get("class_id", Long.class);
            String className = driverRecord.get("class_name", String.class);

            // Add driver-specific condition to the where clause
            var driverWhereCondition = whereCondition.and(Tables.LAPS.DRIVER_ID.eq(driverId));

            // Get total lap count for this driver
            Integer driverTotalLapCount = dsl.selectCount()
                    .from(table)
                    .join(Tables.CAR_ENTRIES).on(Tables.CAR_ENTRIES.ID.eq(Tables.LAPS.CAR_ID))
                    .join(Tables.SESSIONS).on(Tables.SESSIONS.ID.eq(Tables.CAR_ENTRIES.SESSION_ID))
                    .where(driverWhereCondition)
                    .fetchOne(0, Integer.class);

            if (driverTotalLapCount == null || driverTotalLapCount == 0) {
                // Skip driver if no laps found
                continue;
            }

            // Calculate how many laps to include in the average based on percentage
            int driverTopCount = Math.max(1, (int) Math.ceil(driverTotalLapCount * percentage / 100.0));

            // Calculate fastest lap time (minimum)
            Field<BigDecimal> minField = DSL.min(Tables.LAPS.LAP_TIME_SECONDS).as("fastest_lap_time");

            // Calculate median lap time
            Field<BigDecimal> medianField = DSL.field(
                    "percentile_cont(0.5) within group (order by {0})",
                    BigDecimal.class,
                    Tables.LAPS.LAP_TIME_SECONDS
            ).as("median_lap_time");

            // Calculate average of top percentage of lap times using a proper jOOQ subquery
            var driverTopLapsSubquery = dsl.select(Tables.LAPS.LAP_TIME_SECONDS.as("lap_time"))
                    .from(table)
                    .join(Tables.CAR_ENTRIES).on(Tables.CAR_ENTRIES.ID.eq(Tables.LAPS.CAR_ID))
                    .join(Tables.SESSIONS).on(Tables.SESSIONS.ID.eq(Tables.CAR_ENTRIES.SESSION_ID))
                    .where(driverWhereCondition)
                    .orderBy(Tables.LAPS.LAP_TIME_SECONDS.asc())
                    .limit(driverTopCount);

            Field<BigDecimal> avgField = dsl.select(DSL.avg(DSL.field("lap_time", BigDecimal.class)))
                    .from(driverTopLapsSubquery)
                    .asField("avg_lap_time");

            // Execute the query to get the statistics for this driver
            Record4<BigDecimal, BigDecimal, BigDecimal, Integer> driverResult = dsl.select(
                            avgField,
                            minField,
                            medianField,
                            DSL.val(driverTotalLapCount).as("total_lap_count")
                    )
                    .from(table)
                    .join(Tables.CAR_ENTRIES).on(Tables.CAR_ENTRIES.ID.eq(Tables.LAPS.CAR_ID))
                    .join(Tables.SESSIONS).on(Tables.SESSIONS.ID.eq(Tables.CAR_ENTRIES.SESSION_ID))
                    .where(driverWhereCondition)
                    .fetchOne();

            if (driverResult == null) {
                // Skip driver if no results
                continue;
            }

            // Format the lap times as "m:ss.SSS"
            String averageLapTime = formatLapTime(driverResult.get(avgField));
            String fastestLapTime = formatLapTime(driverResult.get(minField));
            String medianLapTime = formatLapTime(driverResult.get(medianField));

            // Create and add the driver-specific analysis DTO
            DriverLapTimeAnalysisDTO driverAnalysis = new DriverLapTimeAnalysisDTO(
                    driverId, driverName, nationality,
                    driverCarId, carNumber, carModel,
                    teamId, teamName,
                    driverClassId, className,
                    averageLapTime, fastestLapTime, medianLapTime,
                    driverTotalLapCount
            );

            driverAnalyses.add(driverAnalysis);
        }

        // Sort the driver analyses by average lap time (for paging purposes)
        driverAnalyses.sort((a, b) -> {
            // Parse the lap times back to comparable values
            // This is a simple string comparison that works because the format is consistent
            return a.getAverageLapTime().compareTo(b.getAverageLapTime());
        });

        return driverAnalyses;
    }

    @Override
    public List<DriverLapTimesDTO> findLapTimesForDriversInSession(Long sessionId, List<Long> driverIds) {
        if (driverIds == null || driverIds.isEmpty()) {
            return new ArrayList<>();
        }

        // Query to get lap times for the specified drivers in the session
        // This joins with all necessary tables to get complete information
        Result<?> result = dsl.select(
                        Tables.LAPS.ID,
                        Tables.LAPS.LAP_NUMBER,
                        Tables.LAPS.LAP_TIME_SECONDS,
                        Tables.LAPS.SESSION_ELAPSED_SECONDS,
                        Tables.LAPS.TIMESTAMP,
                        Tables.LAPS.AVERAGE_SPEED_KPH,
                        Tables.DRIVERS.ID.as("driver_id"),
                        DSL.concat(Tables.DRIVERS.FIRST_NAME, DSL.val(" "), Tables.DRIVERS.LAST_NAME).as("driver_name"),
                        Tables.CAR_ENTRIES.NUMBER.as("car_number"),
                        Tables.TEAMS.NAME.as("team_name"),
                        Tables.CAR_MODELS.NAME.as("car_model"),
                        Tables.CLASSES.NAME.as("class_name")
                )
                .from(table)
                .join(Tables.CAR_ENTRIES).on(Tables.CAR_ENTRIES.ID.eq(Tables.LAPS.CAR_ID))
                .join(Tables.DRIVERS).on(Tables.DRIVERS.ID.eq(Tables.LAPS.DRIVER_ID))
                .join(Tables.TEAMS).on(Tables.TEAMS.ID.eq(Tables.CAR_ENTRIES.TEAM_ID))
                .join(Tables.CAR_MODELS).on(Tables.CAR_MODELS.ID.eq(Tables.CAR_ENTRIES.CAR_MODEL_ID))
                .join(Tables.CLASSES).on(Tables.CLASSES.ID.eq(Tables.CAR_ENTRIES.CLASS_ID))
                .where(Tables.CAR_ENTRIES.SESSION_ID.eq(sessionId))
                .and(Tables.LAPS.DRIVER_ID.in(driverIds))
                .orderBy(Tables.LAPS.DRIVER_ID, Tables.LAPS.LAP_NUMBER)
                .fetch();

        // Group lap times by driver
        Map<Long, List<LapTimeDetailDTO>> driverLapTimesMap = new HashMap<>();
        Map<Long, DriverInfo> driverInfoMap = new HashMap<>();

        for (Record record : result) {
            Long driverId = record.get("driver_id", Long.class);

            // Create lap time detail DTO
            LapTimeDetailDTO lapTimeDetail = new LapTimeDetailDTO(
                    record.get(Tables.LAPS.ID),
                    record.get(Tables.LAPS.LAP_NUMBER),
                    formatLapTime(record.get(Tables.LAPS.LAP_TIME_SECONDS)),
                    record.get(Tables.LAPS.LAP_TIME_SECONDS),
                    record.get(Tables.LAPS.SESSION_ELAPSED_SECONDS),
                    record.get(Tables.LAPS.TIMESTAMP),
                    record.get(Tables.LAPS.AVERAGE_SPEED_KPH)
            );

            // Add to driver's lap times list
            driverLapTimesMap.computeIfAbsent(driverId, k -> new ArrayList<>()).add(lapTimeDetail);

            // Store driver info (will be the same for all laps of the same driver)
            if (!driverInfoMap.containsKey(driverId)) {
                driverInfoMap.put(driverId, new DriverInfo(
                        record.get("driver_name", String.class),
                        record.get("car_number", String.class),
                        record.get("team_name", String.class),
                        record.get("car_model", String.class),
                        record.get("class_name", String.class)
                ));
            }
        }

        // Create DriverLapTimesDTO for each driver
        List<DriverLapTimesDTO> driverLapTimes = new ArrayList<>();
        for (Long driverId : driverIds) {
            List<LapTimeDetailDTO> lapTimes = driverLapTimesMap.get(driverId);
            DriverInfo driverInfo = driverInfoMap.get(driverId);

            if (lapTimes != null && driverInfo != null) {
                DriverLapTimesDTO driverLapTimesDTO = new DriverLapTimesDTO(
                        driverId,
                        driverInfo.driverName,
                        driverInfo.carNumber,
                        driverInfo.teamName,
                        driverInfo.carModel,
                        driverInfo.className,
                        lapTimes
                );
                driverLapTimes.add(driverLapTimesDTO);
            }
        }

        return driverLapTimes;
    }

    @Override
    public List<Lap> saveAll(List<Lap> laps) {
        if (laps == null || laps.isEmpty()) return new ArrayList<>();
        // Batch insert using JOOQ
        var insertStep = dsl.insertInto(table)
            .columns(
                Tables.LAPS.CAR_ID,
                Tables.LAPS.DRIVER_ID,
                Tables.LAPS.LAP_NUMBER,
                Tables.LAPS.LAP_TIME_SECONDS,
                Tables.LAPS.SESSION_ELAPSED_SECONDS,
                Tables.LAPS.TIMESTAMP,
                    Tables.LAPS.AVERAGE_SPEED_KPH
            );
        for (Lap lap : laps) {
            insertStep = insertStep.values(
                lap.getCarId(),
                lap.getDriverId(),
                lap.getLapNumber(),
                lap.getLapTimeSeconds(),
                lap.getSessionElapsedSeconds(),
                lap.getTimestamp(),
                    lap.getAverageSpeedKph()
            );
        }
        var result = insertStep.returning().fetch();
        List<Lap> savedLaps = new ArrayList<>();
        for (Record record : result) {
            savedLaps.add(mapToEntity(record));
        }
        return savedLaps;
    }

    /**
     * Helper class to store driver information.
     */
    private static class DriverInfo {
        final String driverName;
        final String carNumber;
        final String teamName;
        final String carModel;
        final String className;

        DriverInfo(String driverName, String carNumber, String teamName, String carModel, String className) {
            this.driverName = driverName;
            this.carNumber = carNumber;
            this.teamName = teamName;
            this.carModel = carModel;
            this.className = className;
        }
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
