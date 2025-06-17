package com.arjunakankipati.racingstatanalysis.repository.impl;

import com.arjunakankipati.racingstatanalysis.jooq.Tables;
import com.arjunakankipati.racingstatanalysis.model.Lap;
import com.arjunakankipati.racingstatanalysis.repository.LapRepository;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


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
        var lapRec = record.into(Tables.LAPS);
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
                        Tables.LAPS.CAR_ID,
                        Tables.LAPS.DRIVER_ID,
                        Tables.LAPS.LAP_NUMBER,
                        Tables.LAPS.LAP_TIME_SECONDS,
                        Tables.LAPS.SESSION_ELAPSED_SECONDS,
                        Tables.LAPS.TIMESTAMP,
                        Tables.LAPS.AVERAGE_SPEED_KPH,
                        Tables.LAPS.IS_VALID,
                        Tables.LAPS.IS_PERSONAL_BEST,
                        Tables.LAPS.IS_SESSION_BEST,
                        Tables.LAPS.INVALIDATION_REASON
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
                .set(Tables.LAPS.IS_VALID, lap.getIsValid())
                .set(Tables.LAPS.IS_PERSONAL_BEST, lap.getIsPersonalBest())
                .set(Tables.LAPS.IS_SESSION_BEST, lap.getIsSessionBest())
                .set(Tables.LAPS.INVALIDATION_REASON, lap.getInvalidationReason())
                .where(idField.eq(lap.getId()))
                .execute();
    }

    @Override
    public List<Lap> findByCarId(Long carId) {
        return dsl.select()
                .from(table)
                .where(Tables.LAPS.CAR_ID.eq(carId))
                .fetch()
                .map(this::mapToEntity);
    }

    @Override
    public List<Lap> findByDriverId(Long driverId) {
        return dsl.select()
                .from(table)
                .where(Tables.LAPS.DRIVER_ID.eq(driverId))
                .fetch()
                .map(this::mapToEntity);
    }

    @Override
    public Optional<Lap> findByCarIdAndLapNumber(Long carId, Integer lapNumber) {
        Record record = dsl.select()
                .from(table)
                .where(Tables.LAPS.CAR_ID.eq(carId))
                .and(Tables.LAPS.LAP_NUMBER.eq(lapNumber))
                .fetchOne();

        return Optional.ofNullable(record)
                .map(this::mapToEntity);
    }

    @Override
    public List<Lap> findByCarIdAndIsValidTrue(Long carId) {
        return dsl.select()
                .from(table)
                .where(Tables.LAPS.CAR_ID.eq(carId))
                .and(Tables.LAPS.IS_VALID.eq(true))
                .fetch()
                .map(this::mapToEntity);
    }

    @Override
    public List<Lap> findByDriverIdAndIsValidTrue(Long driverId) {
        return dsl.select()
                .from(table)
                .where(Tables.LAPS.DRIVER_ID.eq(driverId))
                .and(Tables.LAPS.IS_VALID.eq(true))
                .fetch()
                .map(this::mapToEntity);
    }

    @Override
    public List<Lap> findByCarIdAndIsPersonalBestTrue(Long carId) {
        return dsl.select()
                .from(table)
                .where(Tables.LAPS.CAR_ID.eq(carId))
                .and(Tables.LAPS.IS_PERSONAL_BEST.eq(true))
                .fetch()
                .map(this::mapToEntity);
    }

    @Override
    public List<Lap> findByCarIdAndIsSessionBestTrue(Long carId) {
        return dsl.select()
                .from(table)
                .where(Tables.LAPS.CAR_ID.eq(carId))
                .and(Tables.LAPS.IS_SESSION_BEST.eq(true))
                .fetch()
                .map(this::mapToEntity);
    }

    @Override
    public List<Lap> findTopLapsByCarId(Long carId, int limit) {
        return dsl.select()
                .from(table)
                .where(Tables.LAPS.CAR_ID.eq(carId))
                .and(Tables.LAPS.IS_VALID.eq(true))
                .orderBy(Tables.LAPS.LAP_TIME_SECONDS.asc())
                .limit(limit)
                .fetch()
                .map(this::mapToEntity);
    }

    @Override
    public List<Lap> findTopLapsByDriverId(Long driverId, int limit) {
        return dsl.select()
                .from(table)
                .where(Tables.LAPS.DRIVER_ID.eq(driverId))
                .and(Tables.LAPS.IS_VALID.eq(true))
                .orderBy(Tables.LAPS.LAP_TIME_SECONDS.asc())
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
        var query = dsl.select()
                .from(table)
                .join(Tables.CARS).on(Tables.CARS.ID.eq(Tables.LAPS.CAR_ID))
                .join(Tables.SESSIONS).on(Tables.SESSIONS.ID.eq(Tables.CARS.SESSION_ID))
                .join(Tables.EVENTS).on(Tables.EVENTS.ID.eq(Tables.SESSIONS.EVENT_ID));

        // Start with base conditions
        var whereCondition = Tables.LAPS.DRIVER_ID.eq(driverId)
                .and(Tables.LAPS.IS_VALID.eq(isValid));

        // Add optional filters
        if (sessionId.isPresent()) {
            whereCondition = whereCondition.and(Tables.CARS.SESSION_ID.eq(sessionId.get()));
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
        var query = dsl.select()
                .from(table)
                .join(Tables.CARS).on(Tables.CARS.ID.eq(Tables.LAPS.CAR_ID))
                .join(Tables.SESSIONS).on(Tables.SESSIONS.ID.eq(Tables.CARS.SESSION_ID))
                .join(Tables.EVENTS).on(Tables.EVENTS.ID.eq(Tables.SESSIONS.EVENT_ID));

        // Start with base conditions
        var whereCondition = Tables.LAPS.DRIVER_ID.eq(driverId)
                .and(Tables.LAPS.IS_VALID.eq(isValid));

        // Add optional filters
        if (sessionId.isPresent()) {
            whereCondition = whereCondition.and(Tables.CARS.SESSION_ID.eq(sessionId.get()));
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
                .join(Tables.CARS).on(Tables.CARS.ID.eq(Tables.LAPS.CAR_ID))
                .join(Tables.SESSIONS).on(Tables.SESSIONS.ID.eq(Tables.CARS.SESSION_ID))
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
}
