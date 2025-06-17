package com.arjunakankipati.racingstatanalysis.repository.impl;

import com.arjunakankipati.racingstatanalysis.model.Class;
import com.arjunakankipati.racingstatanalysis.repository.ClassRepository;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static org.jooq.impl.DSL.field;

/**
 * Implementation of the ClassRepository interface using JOOQ.
 * Extends BaseRepositoryImpl to inherit common CRUD operations.
 */
@Repository
public class ClassRepositoryImpl extends BaseRepositoryImpl<Class, Long> implements ClassRepository {

    /**
     * Constructor with DSLContext dependency injection.
     *
     * @param dsl the JOOQ DSL context
     */
    @Autowired
    public ClassRepositoryImpl(DSLContext dsl) {
        super(dsl, "classes", "id", Long.class);
    }

    @Override
    protected Class mapToEntity(Record record) {
        if (record == null) {
            return null;
        }

        return new Class(
                record.get(field("id", Long.class)),
                record.get(field("series_id", Long.class)),
                record.get(field("name", String.class)),
                record.get(field("description", String.class))
        );
    }

    @Override
    protected Class insert(Class clazz) {
        Record record = dsl.insertInto(table)
                .columns(
                        field("series_id"),
                        field("name"),
                        field("description")
                )
                .values(
                        clazz.getSeriesId(),
                        clazz.getName(),
                        clazz.getDescription()
                )
                .returningResult(
                        field("id"),
                        field("series_id"),
                        field("name"),
                        field("description")
                )
                .fetchOne();

        return mapToEntity(record);
    }

    @Override
    protected void update(Class clazz) {
        dsl.update(table)
                .set(field("series_id"), clazz.getSeriesId())
                .set(field("name"), clazz.getName())
                .set(field("description"), clazz.getDescription())
                .where(idField.eq(clazz.getId()))
                .execute();
    }

    @Override
    public Optional<Class> findByName(String name) {
        Record record = dsl.select()
                .from(table)
                .where(field("name").eq(name))
                .fetchOne();

        return Optional.ofNullable(record)
                .map(this::mapToEntity);
    }

    @Override
    public List<Class> findBySeriesId(Long seriesId) {
        return dsl.select()
                .from(table)
                .where(field("series_id").eq(seriesId))
                .fetch()
                .map(this::mapToEntity);
    }

    @Override
    public Optional<Class> findBySeriesIdAndName(Long seriesId, String name) {
        Record record = dsl.select()
                .from(table)
                .where(field("series_id").eq(seriesId))
                .and(field("name").eq(name))
                .fetchOne();

        return Optional.ofNullable(record)
                .map(this::mapToEntity);
    }

    @Override
    public List<Class> findByNameContaining(String nameContains) {
        return dsl.select()
                .from(table)
                .where(field("name").like("%" + nameContains + "%"))
                .fetch()
                .map(this::mapToEntity);
    }
}