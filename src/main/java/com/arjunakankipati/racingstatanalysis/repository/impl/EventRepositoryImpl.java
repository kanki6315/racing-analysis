package com.arjunakankipati.racingstatanalysis.repository.impl;

import com.arjunakankipati.racingstatanalysis.jooq.Tables;
import com.arjunakankipati.racingstatanalysis.model.Event;
import com.arjunakankipati.racingstatanalysis.repository.EventRepository;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


/**
 * Implementation of the EventRepository interface using JOOQ.
 * Extends BaseRepositoryImpl to inherit common CRUD operations.
 */
@Repository
public class EventRepositoryImpl extends BaseRepositoryImpl<Event, Long> implements EventRepository {

    /**
     * Constructor with DSLContext dependency injection.
     *
     * @param dsl the JOOQ DSL context
     */
    @Autowired
    public EventRepositoryImpl(DSLContext dsl) {
        super(dsl, "events", "id", Long.class);
    }

    @Override
    protected Event mapToEntity(Record record) {
        if (record == null) {
            return null;
        }

        var eventRec = record.into(Tables.EVENTS);
        return new Event(
                eventRec.getId(),
                eventRec.getSeriesId(),
                eventRec.getName(),
                eventRec.getYear(),
                eventRec.getStartDate(),
                eventRec.getEndDate(),
                eventRec.getDescription());
    }

    @Override
    protected Event insert(Event event) {
        Record record = dsl.insertInto(table)
                .columns(
                        Tables.EVENTS.SERIES_ID,
                        Tables.EVENTS.NAME,
                        Tables.EVENTS.YEAR,
                        Tables.EVENTS.START_DATE,
                        Tables.EVENTS.END_DATE,
                        Tables.EVENTS.DESCRIPTION
                )
                .values(
                        event.getSeriesId(),
                        event.getName(),
                        event.getYear(),
                        event.getStartDate(),
                        event.getEndDate(),
                        event.getDescription()
                )
                .returning()
                .fetchOne();

        return mapToEntity(record);
    }

    @Override
    protected void update(Event event) {
        dsl.update(table)
                .set(Tables.EVENTS.SERIES_ID, event.getSeriesId())
                .set(Tables.EVENTS.NAME, event.getName())
                .set(Tables.EVENTS.YEAR, event.getYear())
                .set(Tables.EVENTS.START_DATE, event.getStartDate())
                .set(Tables.EVENTS.END_DATE, event.getEndDate())
                .set(Tables.EVENTS.DESCRIPTION, event.getDescription())
                .where(idField.eq(event.getId()))
                .execute();
    }

    @Override
    public List<Event> findBySeriesId(Long seriesId) {
        return dsl.select()
                .from(table)
                .where(Tables.EVENTS.SERIES_ID.eq(seriesId))
                .fetch()
                .map(this::mapToEntity);
    }

    @Override
    public List<Event> findBySeriesIdAndYear(Long seriesId, Integer year) {
        return dsl.select()
                .from(table)
                .where(Tables.EVENTS.SERIES_ID.eq(seriesId))
                .and(Tables.EVENTS.YEAR.eq(year))
                .fetch()
                .map(this::mapToEntity);
    }

    @Override
    public Optional<Event> findByNameAndYear(String name, Integer year) {
        Record record = dsl.select()
                .from(table)
                .where(Tables.EVENTS.NAME.eq(name))
                .and(Tables.EVENTS.YEAR.eq(year))
                .fetchOne();

        return Optional.ofNullable(record)
                .map(this::mapToEntity);
    }

    @Override
    public List<Integer> findYearsBySeriesId(Long seriesId) {
        return dsl.selectDistinct(Tables.EVENTS.YEAR)
                .from(table)
                .where(Tables.EVENTS.SERIES_ID.eq(seriesId))
                .orderBy(Tables.EVENTS.YEAR.desc())
                .fetch(Tables.EVENTS.YEAR);
    }
}
