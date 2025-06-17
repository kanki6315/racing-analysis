package com.arjunakankipati.racing_stat_analysis.repository.impl;

import com.arjunakankipati.racing_stat_analysis.model.Lap;
import com.arjunakankipati.racing_stat_analysis.repository.LapRepository;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SortField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.jooq.impl.DSL.field;

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

        return new Lap(
                record.get(field("id", Long.class)),
                record.get(field("car_id", Long.class)),
                record.get(field("driver_id", Long.class)),
                record.get(field("lap_number", Integer.class)),
                record.get(field("lap_time_seconds", BigDecimal.class)),
                record.get(field("session_elapsed_seconds", BigDecimal.class)),
                record.get(field("timestamp", LocalDateTime.class)),
                record.get(field("average_speed_kph", BigDecimal.class)),
                record.get(field("is_valid", Boolean.class)),
                record.get(field("is_personal_best", Boolean.class)),
                record.get(field("is_session_best", Boolean.class)),
                record.get(field("invalidation_reason", String.class))
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
}