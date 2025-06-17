package com.arjunakankipati.racingstatanalysis.repository.impl;

import com.arjunakankipati.racingstatanalysis.model.Event;
import com.arjunakankipati.racingstatanalysis.repository.EventRepository;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.jooq.impl.DSL.field;

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

        return new Event(
                record.get(field("id", Long.class)),
                record.get(field("series_id", Long.class)),
                record.get(field("name", String.class)),
                record.get(field("year", Integer.class)),
                record.get(field("start_date", LocalDate.class)),
                record.get(field("end_date", LocalDate.class)),
                record.get(field("description", String.class))
        );
    }

    @Override
    protected Event insert(Event event) {
        Record record = dsl.insertInto(table)
                .columns(
                        field("series_id"),
                        field("name"),
                        field("year"),
                        field("start_date"),
                        field("end_date"),
                        field("description")
                )
                .values(
                        event.getSeriesId(),
                        event.getName(),
                        event.getYear(),
                        event.getStartDate(),
                        event.getEndDate(),
                        event.getDescription()
                )
                .returningResult(
                        field("id"),
                        field("series_id"),
                        field("name"),
                        field("year"),
                        field("start_date"),
                        field("end_date"),
                        field("description")
                )
                .fetchOne();

        return mapToEntity(record);
    }

    @Override
    protected void update(Event event) {
        dsl.update(table)
                .set(field("series_id"), event.getSeriesId())
                .set(field("name"), event.getName())
                .set(field("year"), event.getYear())
                .set(field("start_date"), event.getStartDate())
                .set(field("end_date"), event.getEndDate())
                .set(field("description"), event.getDescription())
                .where(idField.eq(event.getId()))
                .execute();
    }

    @Override
    public List<Event> findBySeriesId(Long seriesId) {
        return dsl.select()
                .from(table)
                .where(field("series_id").eq(seriesId))
                .fetch()
                .map(this::mapToEntity);
    }

    @Override
    public List<Event> findBySeriesIdAndYear(Long seriesId, Integer year) {
        return dsl.select()
                .from(table)
                .where(field("series_id").eq(seriesId))
                .and(field("year").eq(year))
                .fetch()
                .map(this::mapToEntity);
    }

    @Override
    public Optional<Event> findByNameAndYear(String name, Integer year) {
        Record record = dsl.select()
                .from(table)
                .where(field("name").eq(name))
                .and(field("year").eq(year))
                .fetchOne();

        return Optional.ofNullable(record)
                .map(this::mapToEntity);
    }

    @Override
    public List<Integer> findYearsBySeriesId(Long seriesId) {
        return dsl.selectDistinct(field("year", Integer.class))
                .from(table)
                .where(field("series_id").eq(seriesId))
                .orderBy(field("year").desc())
                .fetch(field("year", Integer.class));
    }
}
