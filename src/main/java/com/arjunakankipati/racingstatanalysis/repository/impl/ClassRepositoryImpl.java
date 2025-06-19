package com.arjunakankipati.racingstatanalysis.repository.impl;

import com.arjunakankipati.racingstatanalysis.jooq.Tables;
import com.arjunakankipati.racingstatanalysis.model.Class;
import com.arjunakankipati.racingstatanalysis.repository.ClassRepository;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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
        super(dsl, Tables.CLASSES, Tables.CLASSES.ID);
    }

    @Override
    protected Class mapToEntity(Record record) {
        if (record == null) {
            return null;
        }

        var classRec = record.into(Tables.CLASSES);
        return new Class(
                classRec.getId(),
                classRec.getSeriesId(),
                classRec.getName(),
                classRec.getDescription()
        );
    }

    @Override
    protected Class insert(Class clazz) {
        Record record = dsl.insertInto(table)
                .columns(
                        Tables.CLASSES.SERIES_ID,
                        Tables.CLASSES.NAME,
                        Tables.CLASSES.DESCRIPTION
                )
                .values(
                        clazz.getSeriesId(),
                        clazz.getName(),
                        clazz.getDescription()
                )
                .returning()
                .fetchOne();

        return mapToEntity(record);
    }

    @Override
    protected void update(Class clazz) {
        dsl.update(table)
                .set(Tables.CLASSES.SERIES_ID, clazz.getSeriesId())
                .set(Tables.CLASSES.NAME, clazz.getName())
                .set(Tables.CLASSES.DESCRIPTION, clazz.getDescription())
                .where(idField.eq(clazz.getId()))
                .execute();
    }

    @Override
    public Optional<Class> findByName(String name) {
        Record record = dsl.select()
                .from(table)
                .where(Tables.CLASSES.NAME.eq(name))
                .fetchOne();

        return Optional.ofNullable(record)
                .map(this::mapToEntity);
    }

    @Override
    public List<Class> findBySeriesId(Long seriesId) {
        return dsl.select()
                .from(table)
                .where(Tables.CLASSES.SERIES_ID.eq(seriesId))
                .fetch()
                .map(this::mapToEntity);
    }

    @Override
    public Optional<Class> findBySeriesIdAndName(Long seriesId, String name) {
        Record record = dsl.select()
                .from(table)
                .where(Tables.CLASSES.SERIES_ID.eq(seriesId))
                .and(Tables.CLASSES.NAME.eq(name))
                .fetchOne();

        return Optional.ofNullable(record)
                .map(this::mapToEntity);
    }

    @Override
    public List<Class> findByNameContaining(String nameContains) {
        return dsl.select()
                .from(table)
                .where(Tables.CLASSES.NAME.like("%" + nameContains + "%"))
                .fetch()
                .map(this::mapToEntity);
    }

    @Override
    public List<Class> findByEventId(Long eventId) {
        return dsl.selectDistinct(Tables.CLASSES.asterisk())
                .from(Tables.CLASSES)
                .join(Tables.CARS).on(Tables.CARS.CLASS_ID.eq(Tables.CLASSES.ID))
                .join(Tables.SESSIONS).on(Tables.SESSIONS.ID.eq(Tables.CARS.SESSION_ID))
                .where(Tables.SESSIONS.EVENT_ID.eq(eventId))
                .fetch()
                .map(this::mapToEntity);
    }
}
