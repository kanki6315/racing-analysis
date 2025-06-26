package com.arjunakankipati.racingstatanalysis.repository.impl;

import com.arjunakankipati.racingstatanalysis.jooq.Tables;
import com.arjunakankipati.racingstatanalysis.model.CarDriver;
import com.arjunakankipati.racingstatanalysis.repository.CarDriverRepository;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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
        super(dsl, Tables.CAR_DRIVERS, Tables.CAR_DRIVERS.ID);
    }

    @Override
    protected CarDriver mapToEntity(Record record) {
        if (record == null) {
            return null;
        }

        var carDriverRec = record.into(Tables.CAR_DRIVERS);
        return new CarDriver(
                carDriverRec.getId(),
                carDriverRec.getCarId(),
                carDriverRec.getDriverId(),
                carDriverRec.getDriverNumber()
        );
    }

    @Override
    protected CarDriver insert(CarDriver carDriver) {
        Record record = dsl.insertInto(table)
                .columns(
                        Tables.CAR_DRIVERS.CAR_ID,
                        Tables.CAR_DRIVERS.DRIVER_ID,
                        Tables.CAR_DRIVERS.DRIVER_NUMBER
                )
                .values(
                        carDriver.getCarId(),
                        carDriver.getDriverId(),
                        carDriver.getDriverNumber()
                )
                .returning()
                .fetchOne();

        return mapToEntity(record);
    }

    @Override
    protected void update(CarDriver carDriver) {
        dsl.update(table)
                .set(Tables.CAR_DRIVERS.CAR_ID, carDriver.getCarId())
                .set(Tables.CAR_DRIVERS.DRIVER_ID, carDriver.getDriverId())
                .set(Tables.CAR_DRIVERS.DRIVER_NUMBER, carDriver.getDriverNumber())
                .where(idField.eq(carDriver.getId()))
                .execute();
    }

    @Override
    public List<CarDriver> findByCarId(Long carId) {
        return dsl.select()
                .from(table)
                .where(Tables.CAR_DRIVERS.CAR_ID.eq(carId))
                .fetch()
                .map(this::mapToEntity);
    }

    @Override
    public Optional<CarDriver> findByCarIdAndDriverId(Long carId, Long driverId) {
        Record record = dsl.select()
                .from(table)
                .where(Tables.CAR_DRIVERS.CAR_ID.eq(carId))
                .and(Tables.CAR_DRIVERS.DRIVER_ID.eq(driverId))
                .fetchOne();

        return Optional.ofNullable(record)
                .map(this::mapToEntity);
    }
}
