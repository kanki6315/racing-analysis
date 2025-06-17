package com.arjunakankipati.racing_stat_analysis.repository.impl;

import com.arjunakankipati.racing_stat_analysis.model.Series;
import com.arjunakankipati.racing_stat_analysis.repository.SeriesRepository;
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
        super(dsl, "series", "id", Long.class);
    }

    @Override
    public Optional<Series> findByName(String name) {
        Record record = dsl.select()
                .from(table)
                .where(field("name").eq(name))
                .fetchOne();

        return Optional.ofNullable(record)
                .map(this::mapToEntity);
    }

    @Override
    protected Series mapToEntity(Record record) {
        if (record == null) {
            return null;
        }

        return new Series(
                record.get(field("id", Long.class)),
                record.get(field("name", String.class)),
                record.get(field("description", String.class))
        );
    }

    @Override
    protected Series insert(Series series) {
        Record record = dsl.insertInto(table)
                .columns(field("name"), field("description"))
                .values(series.getName(), series.getDescription())
                .returningResult(field("id"), field("name"), field("description"))
                .fetchOne();

        return mapToEntity(record);
    }

    @Override
    protected void update(Series series) {
        dsl.update(table)
                .set(field("name"), series.getName())
                .set(field("description"), series.getDescription())
                .where(idField.eq(series.getId()))
                .execute();
    }
}
