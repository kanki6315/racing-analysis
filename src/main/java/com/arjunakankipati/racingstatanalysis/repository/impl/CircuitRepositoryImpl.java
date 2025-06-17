package com.arjunakankipati.racingstatanalysis.repository.impl;

import com.arjunakankipati.racingstatanalysis.jooq.Tables;
import com.arjunakankipati.racingstatanalysis.model.Circuit;
import com.arjunakankipati.racingstatanalysis.repository.CircuitRepository;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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

        var circuitRec = record.into(Tables.CIRCUITS);
        return new Circuit(
                circuitRec.getId(),
                circuitRec.getName(),
                circuitRec.getLengthMeters(),
                circuitRec.getCountry(),
                circuitRec.getLocation(),
                circuitRec.getDescription()
        );
    }

    @Override
    protected Circuit insert(Circuit circuit) {
        Record record = dsl.insertInto(table)
                .columns(
                        Tables.CIRCUITS.NAME,
                        Tables.CIRCUITS.LENGTH_METERS,
                        Tables.CIRCUITS.COUNTRY,
                        Tables.CIRCUITS.LOCATION,
                        Tables.CIRCUITS.DESCRIPTION
                )
                .values(
                        circuit.getName(),
                        circuit.getLengthMeters(),
                        circuit.getCountry(),
                        circuit.getLocation(),
                        circuit.getDescription()
                )
                .returning()
                .fetchOne();

        return mapToEntity(record);
    }

    @Override
    protected void update(Circuit circuit) {
        dsl.update(table)
                .set(Tables.CIRCUITS.NAME, circuit.getName())
                .set(Tables.CIRCUITS.LENGTH_METERS, circuit.getLengthMeters())
                .set(Tables.CIRCUITS.COUNTRY, circuit.getCountry())
                .set(Tables.CIRCUITS.LOCATION, circuit.getLocation())
                .set(Tables.CIRCUITS.DESCRIPTION, circuit.getDescription())
                .where(idField.eq(circuit.getId()))
                .execute();
    }

    @Override
    public Optional<Circuit> findByName(String name) {
        Record record = dsl.select()
                .from(table)
                .where(Tables.CIRCUITS.NAME.eq(name))
                .fetchOne();

        return Optional.ofNullable(record)
                .map(this::mapToEntity);
    }

    @Override
    public List<Circuit> findByCountry(String country) {
        return dsl.select()
                .from(table)
                .where(Tables.CIRCUITS.COUNTRY.eq(country))
                .fetch()
                .map(this::mapToEntity);
    }

    @Override
    public List<Circuit> findByNameContaining(String nameContains) {
        return dsl.select()
                .from(table)
                .where(Tables.CIRCUITS.NAME.like("%" + nameContains + "%"))
                .fetch()
                .map(this::mapToEntity);
    }
}
