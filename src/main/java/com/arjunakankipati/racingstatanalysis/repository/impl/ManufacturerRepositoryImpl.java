package com.arjunakankipati.racingstatanalysis.repository.impl;

import com.arjunakankipati.racingstatanalysis.jooq.Tables;
import com.arjunakankipati.racingstatanalysis.model.Manufacturer;
import com.arjunakankipati.racingstatanalysis.repository.ManufacturerRepository;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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

        var manRec = record.into(Tables.MANUFACTURERS);
        return new Manufacturer(
                manRec.getId(),
                manRec.getName(),
                manRec.getCountry());
    }

    @Override
    protected Manufacturer insert(Manufacturer manufacturer) {
        Record record = dsl.insertInto(table)
                .columns(
                        Tables.MANUFACTURERS.NAME,
                        Tables.MANUFACTURERS.COUNTRY
                )
                .values(
                        manufacturer.getName(),
                        manufacturer.getCountry()
                )
                .returning()
                .fetchOne();

        return mapToEntity(record);
    }

    @Override
    protected void update(Manufacturer manufacturer) {
        dsl.update(table)
                .set(Tables.MANUFACTURERS.NAME, manufacturer.getName())
                .set(Tables.MANUFACTURERS.COUNTRY, manufacturer.getCountry())
                .where(idField.eq(manufacturer.getId()))
                .execute();
    }

    @Override
    public Optional<Manufacturer> findByName(String name) {
        Record record = dsl.select()
                .from(table)
                .where(Tables.MANUFACTURERS.NAME.eq(name))
                .fetchOne();

        return Optional.ofNullable(record)
                .map(this::mapToEntity);
    }

    @Override
    public List<Manufacturer> findByCountry(String country) {
        return dsl.select()
                .from(table)
                .where(Tables.MANUFACTURERS.COUNTRY.eq(country))
                .fetch()
                .map(this::mapToEntity);
    }

    @Override
    public List<Manufacturer> findByNameContaining(String nameContains) {
        return dsl.select()
                .from(table)
                .where(Tables.MANUFACTURERS.NAME.like("%" + nameContains + "%"))
                .fetch()
                .map(this::mapToEntity);
    }
}