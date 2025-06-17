package com.arjunakankipati.racingstatanalysis.repository.impl;

import com.arjunakankipati.racingstatanalysis.jooq.tables.records.LapsRecord;
import com.arjunakankipati.racingstatanalysis.model.Lap;
import com.arjunakankipati.racingstatanalysis.repository.LapRepository;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

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
        super(dsl, "laps", "id", Long.class);
    }

    @Override
    protected Lap mapToEntity(Record record) {
        if (record == null) {
            return null;
        }

        var lapRec = (LapsRecord) record;
        return new Lap(
                lapRec.getId(),
                lapRec.getCarId(),
                lapRec.getDriverId(),
                lapRec.getLapNumber(),
                lapRec.getLapTimeSeconds(),
                lapRec.getSessionElapsedSeconds(),
                lapRec.getTimestamp(),
                lapRec.getAverageSpeedKph(),
                lapRec.getIsValid(),
                lapRec.getIsPersonalBest(),
                lapRec.getIsSessionBest(),
                lapRec.getInvalidationReason()
        );
    }

    @Override
    protected Lap insert(Lap lap) {
        Record record = dsl.insertInto(table)
                .columns(
                        field("car_id"),
                        field("driver_id"),
                        field("lap_number"),
                        field("lap_time_seconds"),
                        field("session_elapsed_seconds"),
                        field("timestamp"),
                        field("average_speed_kph"),
                        field("is_valid"),
                        field("is_personal_best"),
                        field("is_session_best"),
                        field("invalidation_reason")
                )
                .values(
                        lap.getCarId(),
                        lap.getDriverId(),
                        lap.getLapNumber(),
                        lap.getLapTimeSeconds(),
                        lap.getSessionElapsedSeconds(),
                        lap.getTimestamp(),
                        lap.getAverageSpeedKph(),
                        lap.getIsValid(),
                        lap.getIsPersonalBest(),
                        lap.getIsSessionBest(),
                        lap.getInvalidationReason()
                )
                .returningResult(
                        field("id"),
                        field("car_id"),
                        field("driver_id"),
                        field("lap_number"),
                        field("lap_time_seconds"),
                        field("session_elapsed_seconds"),
                        field("timestamp"),
                        field("average_speed_kph"),
                        field("is_valid"),
                        field("is_personal_best"),
                        field("is_session_best"),
                        field("invalidation_reason")
                )
                .fetchOne();

        return mapToEntity(record);
    }

    @Override
    protected void update(Lap lap) {
        dsl.update(table)
                .set(field("car_id"), lap.getCarId())
                .set(field("driver_id"), lap.getDriverId())
                .set(field("lap_number"), lap.getLapNumber())
                .set(field("lap_time_seconds"), lap.getLapTimeSeconds())
                .set(field("session_elapsed_seconds"), lap.getSessionElapsedSeconds())
                .set(field("timestamp"), lap.getTimestamp())
                .set(field("average_speed_kph"), lap.getAverageSpeedKph())
                .set(field("is_valid"), lap.getIsValid())
                .set(field("is_personal_best"), lap.getIsPersonalBest())
                .set(field("is_session_best"), lap.getIsSessionBest())
                .set(field("invalidation_reason"), lap.getInvalidationReason())
                .where(idField.eq(lap.getId()))
                .execute();
    }

    @Override
    public List<Lap> findByCarId(Long carId) {
        return dsl.select()
                .from(table)
                .where(field("car_id").eq(carId))
                .fetch()
                .map(this::mapToEntity);
    }

    @Override
    public List<Lap> findByDriverId(Long driverId) {
        return dsl.select()
                .from(table)
                .where(field("driver_id").eq(driverId))
                .fetch()
                .map(this::mapToEntity);
    }

    @Override
    public Optional<Lap> findByCarIdAndLapNumber(Long carId, Integer lapNumber) {
        Record record = dsl.select()
                .from(table)
                .where(field("car_id").eq(carId))
                .and(field("lap_number").eq(lapNumber))
                .fetchOne();

        return Optional.ofNullable(record)
                .map(this::mapToEntity);
    }

    @Override
    public List<Lap> findByCarIdAndIsValidTrue(Long carId) {
        return dsl.select()
                .from(table)
                .where(field("car_id").eq(carId))
                .and(field("is_valid").eq(true))
                .fetch()
                .map(this::mapToEntity);
    }

    @Override
    public List<Lap> findByDriverIdAndIsValidTrue(Long driverId) {
        return dsl.select()
                .from(table)
                .where(field("driver_id").eq(driverId))
                .and(field("is_valid").eq(true))
                .fetch()
                .map(this::mapToEntity);
    }

    @Override
    public List<Lap> findByCarIdAndIsPersonalBestTrue(Long carId) {
        return dsl.select()
                .from(table)
                .where(field("car_id").eq(carId))
                .and(field("is_personal_best").eq(true))
                .fetch()
                .map(this::mapToEntity);
    }

    @Override
    public List<Lap> findByCarIdAndIsSessionBestTrue(Long carId) {
        return dsl.select()
                .from(table)
                .where(field("car_id").eq(carId))
                .and(field("is_session_best").eq(true))
                .fetch()
                .map(this::mapToEntity);
    }

    @Override
    public List<Lap> findTopLapsByCarId(Long carId, int limit) {
        return dsl.select()
                .from(table)
                .where(field("car_id").eq(carId))
                .and(field("is_valid").eq(true))
                .orderBy(field("lap_time_seconds").asc())
                .limit(limit)
                .fetch()
                .map(this::mapToEntity);
    }

    @Override
    public List<Lap> findTopLapsByDriverId(Long driverId, int limit) {
        return dsl.select()
                .from(table)
                .where(field("driver_id").eq(driverId))
                .and(field("is_valid").eq(true))
                .orderBy(field("lap_time_seconds").asc())
                .limit(limit)
                .fetch()
                .map(this::mapToEntity);
    }

    @Override
    public List<Lap> findFilteredLaps(Long driverId, boolean isValid,
                                      Optional<Long> sessionId,
                                      Optional<Long> eventId,
                                      Optional<Integer> year,
                                      Optional<Long> seriesId) {
        // Build the query with joins
        var query = dsl.select(field("laps.*"))
                .from(table)
                .join(table("cars")).on(field("cars.id").eq(field("laps.car_id")))
                .join(table("sessions")).on(field("sessions.id").eq(field("cars.session_id")))
                .join(table("events")).on(field("events.id").eq(field("sessions.event_id")));

        // Start with base conditions
        var whereCondition = field("laps.driver_id").eq(driverId)
                .and(field("laps.is_valid").eq(isValid));

        // Add optional filters
        if (sessionId.isPresent()) {
            whereCondition = whereCondition.and(field("cars.session_id").eq(sessionId.get()));
        }

        if (eventId.isPresent()) {
            whereCondition = whereCondition.and(field("sessions.event_id").eq(eventId.get()));
        }

        if (year.isPresent()) {
            whereCondition = whereCondition.and(field("events.year").eq(year.get()));
        }

        if (seriesId.isPresent()) {
            whereCondition = whereCondition.and(field("events.series_id").eq(seriesId.get()));
        }

        // Execute the query with the where condition
        return query.where(whereCondition)
                .fetch()
                .map(this::mapToEntity);
    }

    @Override
    public List<Lap> findTopPercentageLapsByDriverId(Long driverId, boolean isValid, int percentage,
                                                     Optional<Long> sessionId,
                                                     Optional<Long> eventId,
                                                     Optional<Integer> year,
                                                     Optional<Long> seriesId) {
        // Build the query with joins
        var query = dsl.select(field("laps.*"))
                .from(table)
                .join(table("cars")).on(field("cars.id").eq(field("laps.car_id")))
                .join(table("sessions")).on(field("sessions.id").eq(field("cars.session_id")))
                .join(table("events")).on(field("events.id").eq(field("sessions.event_id")));

        // Start with base conditions
        var whereCondition = field("laps.driver_id").eq(driverId)
                .and(field("laps.is_valid").eq(isValid));

        // Add optional filters
        if (sessionId.isPresent()) {
            whereCondition = whereCondition.and(field("cars.session_id").eq(sessionId.get()));
        }

        if (eventId.isPresent()) {
            whereCondition = whereCondition.and(field("sessions.event_id").eq(eventId.get()));
        }

        if (year.isPresent()) {
            whereCondition = whereCondition.and(field("events.year").eq(year.get()));
        }

        if (seriesId.isPresent()) {
            whereCondition = whereCondition.and(field("events.series_id").eq(seriesId.get()));
        }

        // First, get the total count of laps that match the criteria
        long totalLaps = dsl.selectCount()
                .from(table)
                .join(table("cars")).on(field("cars.id").eq(field("laps.car_id")))
                .join(table("sessions")).on(field("sessions.id").eq(field("cars.session_id")))
                .join(table("events")).on(field("events.id").eq(field("sessions.event_id")))
                .where(whereCondition)
                .fetchOne(0, Long.class);

        // Calculate how many laps to return based on percentage
        int topCount = Math.max(1, (int) Math.ceil(totalLaps * percentage / 100.0));

        // Execute the query with the where condition, order by lap time, and limit to top percentage
        return query.where(whereCondition)
                .orderBy(field("laps.lap_time_seconds").asc())
                .limit(topCount)
                .fetch()
                .map(this::mapToEntity);
    }
}
