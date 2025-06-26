package com.arjunakankipati.racingstatanalysis.repository.impl;

import com.arjunakankipati.racingstatanalysis.jooq.Tables;
import com.arjunakankipati.racingstatanalysis.model.CarEntry;
import com.arjunakankipati.racingstatanalysis.repository.CarEntryRepository;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of CarEntryRepository using JOOQ.
 */
@Repository
public class CarEntryRepositoryImpl extends BaseRepositoryImpl<CarEntry, Long> implements CarEntryRepository {

    @Autowired
    public CarEntryRepositoryImpl(DSLContext dsl) {
        super(dsl, Tables.CAR_ENTRIES, Tables.CAR_ENTRIES.ID);
    }

    @Override
    protected CarEntry mapToEntity(Record record) {
        if (record == null) {
            return null;
        }

        var carEntryRec = record.into(Tables.CAR_ENTRIES);
        return new CarEntry(
                carEntryRec.getId(),
                carEntryRec.getSessionId(),
                carEntryRec.getTeamId(),
                carEntryRec.getClassId(),
                carEntryRec.getCarModelId(),
                carEntryRec.getNumber(),
                carEntryRec.getTireSupplier()
        );
    }

    @Override
    protected CarEntry insert(CarEntry carEntry) {
        Record record = dsl.insertInto(table)
                .columns(
                        Tables.CAR_ENTRIES.SESSION_ID,
                        Tables.CAR_ENTRIES.TEAM_ID,
                        Tables.CAR_ENTRIES.CLASS_ID,
                        Tables.CAR_ENTRIES.CAR_MODEL_ID,
                        Tables.CAR_ENTRIES.NUMBER,
                        Tables.CAR_ENTRIES.TIRE_SUPPLIER
                )
                .values(
                        carEntry.getSessionId(),
                        carEntry.getTeamId(),
                        carEntry.getClassId(),
                        carEntry.getCarModelId(),
                        carEntry.getNumber(),
                        carEntry.getTireSupplier()
                )
                .returning()
                .fetchOne();

        return mapToEntity(record);
    }

    @Override
    protected void update(CarEntry carEntry) {
        dsl.update(table)
                .set(Tables.CAR_ENTRIES.SESSION_ID, carEntry.getSessionId())
                .set(Tables.CAR_ENTRIES.TEAM_ID, carEntry.getTeamId())
                .set(Tables.CAR_ENTRIES.CLASS_ID, carEntry.getClassId())
                .set(Tables.CAR_ENTRIES.CAR_MODEL_ID, carEntry.getCarModelId())
                .set(Tables.CAR_ENTRIES.NUMBER, carEntry.getNumber())
                .set(Tables.CAR_ENTRIES.TIRE_SUPPLIER, carEntry.getTireSupplier())
                .where(idField.eq(carEntry.getId()))
                .execute();
    }

    @Override
    public List<CarEntry> findBySessionId(Long sessionId) {
        return dsl.select()
                .from(table)
                .where(Tables.CAR_ENTRIES.SESSION_ID.eq(sessionId))
                .fetch()
                .map(this::mapToEntity);
    }


    @Override
    public List<CarEntry> findByEventIdAndClassId(Long eventId, Long classId) {
        return dsl.selectDistinct(Tables.CAR_ENTRIES.asterisk())
                .from(Tables.CAR_ENTRIES)
                .join(Tables.SESSIONS).on(Tables.SESSIONS.ID.eq(Tables.CAR_ENTRIES.SESSION_ID))
                .where(Tables.SESSIONS.EVENT_ID.eq(eventId))
                .and(Tables.CAR_ENTRIES.CLASS_ID.eq(classId))
                .fetch()
                .map(this::mapToEntity);
    }

    @Override
    public Optional<CarEntry> findBySessionIdAndNumber(Long sessionId, String number) {
        Record record = dsl.select()
                .from(table)
                .where(Tables.CAR_ENTRIES.SESSION_ID.eq(sessionId))
                .and(Tables.CAR_ENTRIES.NUMBER.eq(number))
                .fetchOne();

        return Optional.ofNullable(record)
                .map(this::mapToEntity);
    }
} 