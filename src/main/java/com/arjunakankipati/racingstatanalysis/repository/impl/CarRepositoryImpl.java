package com.arjunakankipati.racingstatanalysis.repository.impl;

import com.arjunakankipati.racingstatanalysis.jooq.Tables;
import com.arjunakankipati.racingstatanalysis.model.Car;
import com.arjunakankipati.racingstatanalysis.repository.CarRepository;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of the CarRepository interface using JOOQ.
 * Extends BaseRepositoryImpl to inherit common CRUD operations.
 */
@Repository
public class CarRepositoryImpl extends BaseRepositoryImpl<Car, Long> implements CarRepository {

    /**
     * Constructor with DSLContext dependency injection.
     *
     * @param dsl the JOOQ DSL context
     */
    @Autowired
    public CarRepositoryImpl(DSLContext dsl) {
        super(dsl, "cars", "id", Long.class);
    }

    @Override
    protected Car mapToEntity(Record record) {
        if (record == null) {
            return null;
        }

        var carRec = record.into(Tables.CARS);
        return new Car(
                carRec.getId(),
                carRec.getSessionId(),
                carRec.getTeamId(),
                carRec.getClassId(),
                carRec.getManufacturerId(),
                carRec.getNumber(),
                carRec.getModel(),
                carRec.getTireSupplier()
        );
    }

    @Override
    protected Car insert(Car car) {
        Record record = dsl.insertInto(table)
                .columns(
                        Tables.CARS.SESSION_ID,
                        Tables.CARS.TEAM_ID,
                        Tables.CARS.CLASS_ID,
                        Tables.CARS.MANUFACTURER_ID,
                        Tables.CARS.NUMBER,
                        Tables.CARS.MODEL,
                        Tables.CARS.TIRE_SUPPLIER
                )
                .values(
                        car.getSessionId(),
                        car.getTeamId(),
                        car.getClassId(),
                        car.getManufacturerId(),
                        car.getNumber(),
                        car.getModel(),
                        car.getTireSupplier()
                )
                .returning()
                .fetchOne();

        return mapToEntity(record);
    }

    @Override
    protected void update(Car car) {
        dsl.update(table)
                .set(Tables.CARS.SESSION_ID, car.getSessionId())
                .set(Tables.CARS.TEAM_ID, car.getTeamId())
                .set(Tables.CARS.CLASS_ID, car.getClassId())
                .set(Tables.CARS.MANUFACTURER_ID, car.getManufacturerId())
                .set(Tables.CARS.NUMBER, car.getNumber())
                .set(Tables.CARS.MODEL, car.getModel())
                .set(Tables.CARS.TIRE_SUPPLIER, car.getTireSupplier())
                .where(idField.eq(car.getId()))
                .execute();
    }

    @Override
    public List<Car> findBySessionId(Long sessionId) {
        return dsl.select()
                .from(table)
                .where(Tables.CARS.SESSION_ID.eq(sessionId))
                .fetch()
                .map(this::mapToEntity);
    }

    @Override
    public List<Car> findByTeamId(Long teamId) {
        return dsl.select()
                .from(table)
                .where(Tables.CARS.TEAM_ID.eq(teamId))
                .fetch()
                .map(this::mapToEntity);
    }

    @Override
    public List<Car> findByClassId(Long classId) {
        return dsl.select()
                .from(table)
                .where(Tables.CARS.CLASS_ID.eq(classId))
                .fetch()
                .map(this::mapToEntity);
    }

    @Override
    public List<Car> findByManufacturerId(Long manufacturerId) {
        return dsl.select()
                .from(table)
                .where(Tables.CARS.MANUFACTURER_ID.eq(manufacturerId))
                .fetch()
                .map(this::mapToEntity);
    }

    @Override
    public Optional<Car> findBySessionIdAndNumber(Long sessionId, String number) {
        Record record = dsl.select()
                .from(table)
                .where(Tables.CARS.SESSION_ID.eq(sessionId))
                .and(Tables.CARS.NUMBER.eq(number))
                .fetchOne();

        return Optional.ofNullable(record)
                .map(this::mapToEntity);
    }

    @Override
    public List<Car> findByTireSupplier(String tireSupplier) {
        return dsl.select()
                .from(table)
                .where(Tables.CARS.TIRE_SUPPLIER.eq(tireSupplier))
                .fetch()
                .map(this::mapToEntity);
    }
}
