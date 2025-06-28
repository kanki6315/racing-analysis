package com.arjunakankipati.racingstatanalysis.repository.impl;

import com.arjunakankipati.racingstatanalysis.jooq.Tables;
import com.arjunakankipati.racingstatanalysis.model.Series;
import com.arjunakankipati.racingstatanalysis.repository.SeriesRepository;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static org.jooq.impl.DSL.field;

/**
 * Implementation of the SeriesRepository interface using JOOQ.
 * Extends BaseRepositoryImpl to inherit common CRUD operations.
 */
@Repository
public class SeriesRepositoryImpl extends BaseRepositoryImpl<Series, Long> implements SeriesRepository {

    /**
     * Constructor with DSLContext dependency injection.
     *
     * @param dsl the JOOQ DSL context
     */
    @Autowired
    public SeriesRepositoryImpl(DSLContext dsl) {
        super(dsl, Tables.SERIES, Tables.SERIES.ID);
    }

    @Override
    public Optional<Series> findByName(String name) {
        Record record = dsl.select()
                .from(table)
                .where(field("name").equalIgnoreCase(name))
                .fetchOne();

        return Optional.ofNullable(record)
                .map(this::mapToEntity);
    }

    @Override
    protected Series mapToEntity(Record record) {
        if (record == null) {
            return null;
        }

        var seriesRec = record.into(Tables.SERIES);
        return new Series(
                seriesRec.getId(),
                seriesRec.getName(),
                seriesRec.getDescription()
        );
    }

    @Override
    protected Series insert(Series series) {
        Record record = dsl.insertInto(table)
                .columns(Tables.SERIES.NAME, Tables.SERIES.DESCRIPTION)
                .values(series.getName(), series.getDescription())
                .returning()
                .fetchOne();

        return mapToEntity(record);
    }

    @Override
    protected void update(Series series) {
        dsl.update(table)
                .set(Tables.SERIES.NAME, series.getName())
                .set(Tables.SERIES.DESCRIPTION, series.getDescription())
                .where(idField.eq(series.getId()))
                .execute();
    }
}
