package com.arjunakankipati.racingstatanalysis.repository.impl;

import com.arjunakankipati.racingstatanalysis.model.Manufacturer;
import com.arjunakankipati.racingstatanalysis.repository.ManufacturerRepository;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static org.jooq.impl.DSL.field;

/**
 * Implementation of the ManufacturerRepository interface using JOOQ.
 * Extends BaseRepositoryImpl to inherit common CRUD operations.
 */
@Repository
public class ManufacturerRepositoryImpl extends BaseRepositoryImpl<Manufacturer, Long> implements ManufacturerRepository {

    /**
     * Constructor with DSLContext dependency injection.
     *
     * @param dsl the JOOQ DSL context
     */
    @Autowired
    public ManufacturerRepositoryImpl(DSLContext dsl) {
        super(dsl, "manufacturers", "id", Long.class);
    }

    @Override
    protected Manufacturer mapToEntity(Record record) {
        if (record == null) {
            return null;
        }

        return new Manufacturer(
                record.get(field("id", Long.class)),
                record.get(field("name", String.class)),
                record.get(field("country", String.class))
        );
    }

    @Override
    protected Manufacturer insert(Manufacturer manufacturer) {
        Record record = dsl.insertInto(table)
                .columns(
                        field("name"),
                        field("country")
                )
                .values(
                        manufacturer.getName(),
                        manufacturer.getCountry()
                )
                .returningResult(
                        field("id"),
                        field("name"),
                        field("country")
                )
                .fetchOne();

        return mapToEntity(record);
    }

    @Override
    protected void update(Manufacturer manufacturer) {
        dsl.update(table)
                .set(field("name"), manufacturer.getName())
                .set(field("country"), manufacturer.getCountry())
                .where(idField.eq(manufacturer.getId()))
                .execute();
    }

    @Override
    public Optional<Manufacturer> findByName(String name) {
        Record record = dsl.select()
                .from(table)
                .where(field("name").eq(name))
                .fetchOne();

        return Optional.ofNullable(record)
                .map(this::mapToEntity);
    }

    @Override
    public List<Manufacturer> findByCountry(String country) {
        return dsl.select()
                .from(table)
                .where(field("country").eq(country))
                .fetch()
                .map(this::mapToEntity);
    }

    @Override
    public List<Manufacturer> findByNameContaining(String nameContains) {
        return dsl.select()
                .from(table)
                .where(field("name").like("%" + nameContains + "%"))
                .fetch()
                .map(this::mapToEntity);
    }
}