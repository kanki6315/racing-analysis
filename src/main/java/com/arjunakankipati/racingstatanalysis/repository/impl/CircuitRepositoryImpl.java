package com.arjunakankipati.racingstatanalysis.repository.impl;

import com.arjunakankipati.racingstatanalysis.model.Circuit;
import com.arjunakankipati.racingstatanalysis.repository.CircuitRepository;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.jooq.impl.DSL.field;

/**
 * Implementation of the CircuitRepository interface using JOOQ.
 * Extends BaseRepositoryImpl to inherit common CRUD operations.
 */
@Repository
public class CircuitRepositoryImpl extends BaseRepositoryImpl<Circuit, Long> implements CircuitRepository {

    /**
     * Constructor with DSLContext dependency injection.
     *
     * @param dsl the JOOQ DSL context
     */
    @Autowired
    public CircuitRepositoryImpl(DSLContext dsl) {
        super(dsl, "circuits", "id", Long.class);
    }

    @Override
    protected Circuit mapToEntity(Record record) {
        if (record == null) {
            return null;
        }

        return new Circuit(
                record.get(field("id", Long.class)),
                record.get(field("name", String.class)),
                record.get(field("length_meters", BigDecimal.class)),
                record.get(field("country", String.class)),
                record.get(field("location", String.class)),
                record.get(field("description", String.class))
        );
    }

    @Override
    protected Circuit insert(Circuit circuit) {
        Record record = dsl.insertInto(table)
                .columns(
                        field("name"),
                        field("length_meters"),
                        field("country"),
                        field("location"),
                        field("description")
                )
                .values(
                        circuit.getName(),
                        circuit.getLengthMeters(),
                        circuit.getCountry(),
                        circuit.getLocation(),
                        circuit.getDescription()
                )
                .returningResult(
                        field("id"),
                        field("name"),
                        field("length_meters"),
                        field("country"),
                        field("location"),
                        field("description")
                )
                .fetchOne();

        return mapToEntity(record);
    }

    @Override
    protected void update(Circuit circuit) {
        dsl.update(table)
                .set(field("name"), circuit.getName())
                .set(field("length_meters"), circuit.getLengthMeters())
                .set(field("country"), circuit.getCountry())
                .set(field("location"), circuit.getLocation())
                .set(field("description"), circuit.getDescription())
                .where(idField.eq(circuit.getId()))
                .execute();
    }

    @Override
    public Optional<Circuit> findByName(String name) {
        Record record = dsl.select()
                .from(table)
                .where(field("name").eq(name))
                .fetchOne();

        return Optional.ofNullable(record)
                .map(this::mapToEntity);
    }

    @Override
    public List<Circuit> findByCountry(String country) {
        return dsl.select()
                .from(table)
                .where(field("country").eq(country))
                .fetch()
                .map(this::mapToEntity);
    }

    @Override
    public List<Circuit> findByNameContaining(String nameContains) {
        return dsl.select()
                .from(table)
                .where(field("name").like("%" + nameContains + "%"))
                .fetch()
                .map(this::mapToEntity);
    }
}