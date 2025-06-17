package com.arjunakankipati.racing_stat_analysis.repository.impl;

import com.arjunakankipati.racing_stat_analysis.model.Car;
import com.arjunakankipati.racing_stat_analysis.repository.CarRepository;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static org.jooq.impl.DSL.field;

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

        return new Car(
                record.get(field("id", Long.class)),
                record.get(field("session_id", Long.class)),
                record.get(field("team_id", Long.class)),
                record.get(field("class_id", Long.class)),
                record.get(field("manufacturer_id", Long.class)),
                record.get(field("number", String.class)),
                record.get(field("model", String.class)),
                record.get(field("tire_supplier", String.class))
        );
    }

    @Override
    protected Car insert(Car car) {
        Record record = dsl.insertInto(table)
                .columns(
                        field("session_id"),
                        field("team_id"),
                        field("class_id"),
                        field("manufacturer_id"),
                        field("number"),
                        field("model"),
                        field("tire_supplier")
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
                .returningResult(
                        field("id"),
                        field("session_id"),
                        field("team_id"),
                        field("class_id"),
                        field("manufacturer_id"),
                        field("number"),
                        field("model"),
                        field("tire_supplier")
                )
                .fetchOne();

        return mapToEntity(record);
    }

    @Override
    protected void update(Car car) {
        dsl.update(table)
                .set(field("session_id"), car.getSessionId())
                .set(field("team_id"), car.getTeamId())
                .set(field("class_id"), car.getClassId())
                .set(field("manufacturer_id"), car.getManufacturerId())
                .set(field("number"), car.getNumber())
                .set(field("model"), car.getModel())
                .set(field("tire_supplier"), car.getTireSupplier())
                .where(idField.eq(car.getId()))
                .execute();
    }

    @Override
    public List<Car> findBySessionId(Long sessionId) {
        return dsl.select()
                .from(table)
                .where(field("session_id").eq(sessionId))
                .fetch()
                .map(this::mapToEntity);
    }

    @Override
    public List<Car> findByTeamId(Long teamId) {
        return dsl.select()
                .from(table)
                .where(field("team_id").eq(teamId))
                .fetch()
                .map(this::mapToEntity);
    }

    @Override
    public List<Car> findByClassId(Long classId) {
        return dsl.select()
                .from(table)
                .where(field("class_id").eq(classId))
                .fetch()
                .map(this::mapToEntity);
    }

    @Override
    public List<Car> findByManufacturerId(Long manufacturerId) {
        return dsl.select()
                .from(table)
                .where(field("manufacturer_id").eq(manufacturerId))
                .fetch()
                .map(this::mapToEntity);
    }

    @Override
    public Optional<Car> findBySessionIdAndNumber(Long sessionId, String number) {
        Record record = dsl.select()
                .from(table)
                .where(field("session_id").eq(sessionId))
                .and(field("number").eq(number))
                .fetchOne();

        return Optional.ofNullable(record)
                .map(this::mapToEntity);
    }

    @Override
    public List<Car> findByTireSupplier(String tireSupplier) {
        return dsl.select()
                .from(table)
                .where(field("tire_supplier").eq(tireSupplier))
                .fetch()
                .map(this::mapToEntity);
    }
}