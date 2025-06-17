package com.arjunakankipati.racingstatanalysis.repository.impl;

import com.arjunakankipati.racingstatanalysis.model.CarDriver;
import com.arjunakankipati.racingstatanalysis.repository.CarDriverRepository;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static org.jooq.impl.DSL.field;

/**
 * Implementation of the CarDriverRepository interface using JOOQ.
 * Extends BaseRepositoryImpl to inherit common CRUD operations.
 */
@Repository
public class CarDriverRepositoryImpl extends BaseRepositoryImpl<CarDriver, Long> implements CarDriverRepository {

    /**
     * Constructor with DSLContext dependency injection.
     *
     * @param dsl the JOOQ DSL context
     */
    @Autowired
    public CarDriverRepositoryImpl(DSLContext dsl) {
        super(dsl, "car_drivers", "id", Long.class);
    }

    @Override
    protected CarDriver mapToEntity(Record record) {
        if (record == null) {
            return null;
        }

        return new CarDriver(
                record.get(field("id", Long.class)),
                record.get(field("car_id", Long.class)),
                record.get(field("driver_id", Long.class)),
                record.get(field("driver_number", Integer.class))
        );
    }

    @Override
    protected CarDriver insert(CarDriver carDriver) {
        Record record = dsl.insertInto(table)
                .columns(
                        field("car_id"),
                        field("driver_id"),
                        field("driver_number")
                )
                .values(
                        carDriver.getCarId(),
                        carDriver.getDriverId(),
                        carDriver.getDriverNumber()
                )
                .returningResult(
                        field("id"),
                        field("car_id"),
                        field("driver_id"),
                        field("driver_number")
                )
                .fetchOne();

        return mapToEntity(record);
    }

    @Override
    protected void update(CarDriver carDriver) {
        dsl.update(table)
                .set(field("car_id"), carDriver.getCarId())
                .set(field("driver_id"), carDriver.getDriverId())
                .set(field("driver_number"), carDriver.getDriverNumber())
                .where(idField.eq(carDriver.getId()))
                .execute();
    }

    @Override
    public List<CarDriver> findByCarId(Long carId) {
        return dsl.select()
                .from(table)
                .where(field("car_id").eq(carId))
                .fetch()
                .map(this::mapToEntity);
    }

    @Override
    public List<CarDriver> findByDriverId(Long driverId) {
        return dsl.select()
                .from(table)
                .where(field("driver_id").eq(driverId))
                .fetch()
                .map(this::mapToEntity);
    }

    @Override
    public Optional<CarDriver> findByCarIdAndDriverId(Long carId, Long driverId) {
        Record record = dsl.select()
                .from(table)
                .where(field("car_id").eq(carId))
                .and(field("driver_id").eq(driverId))
                .fetchOne();

        return Optional.ofNullable(record)
                .map(this::mapToEntity);
    }

    @Override
    public List<CarDriver> findByDriverNumber(Integer driverNumber) {
        return dsl.select()
                .from(table)
                .where(field("driver_number").eq(driverNumber))
                .fetch()
                .map(this::mapToEntity);
    }

    @Override
    public Optional<CarDriver> findByCarIdAndDriverNumber(Long carId, Integer driverNumber) {
        Record record = dsl.select()
                .from(table)
                .where(field("car_id").eq(carId))
                .and(field("driver_number").eq(driverNumber))
                .fetchOne();

        return Optional.ofNullable(record)
                .map(this::mapToEntity);
    }
}