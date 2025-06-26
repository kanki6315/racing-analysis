package com.arjunakankipati.racingstatanalysis.repository.impl;

import com.arjunakankipati.racingstatanalysis.jooq.Tables;
import com.arjunakankipati.racingstatanalysis.jooq.tables.Results;
import com.arjunakankipati.racingstatanalysis.jooq.tables.records.ResultsRecord;
import com.arjunakankipati.racingstatanalysis.model.Result;
import com.arjunakankipati.racingstatanalysis.repository.ResultRepository;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ResultRepositoryImpl extends BaseRepositoryImpl<Result, Long> implements ResultRepository {
    private static final Results RESULTS = Tables.RESULTS;

    @Autowired
    public ResultRepositoryImpl(DSLContext dsl) {
        super(dsl, RESULTS, RESULTS.ID);
    }

    @Override
    protected Result mapToEntity(Record record) {
        if (record == null) return null;
        ResultsRecord rec = record.into(RESULTS);
        Result result = new Result();
        result.setId(rec.getId());
        result.setSessionId(rec.getSessionId());
        result.setCarEntryId(rec.getCarEntryId());
        result.setCarNumber(rec.getCarNumber());
        result.setTires(rec.getTires());
        result.setStatus(rec.getStatus());
        result.setLaps(rec.getLaps());
        result.setTotalTime(rec.getTotalTime());
        result.setGapFirst(rec.getGapFirst());
        result.setGapPrevious(rec.getGapPrevious());
        result.setFlLapnum(rec.getFlLapnum());
        result.setFlTime(rec.getFlTime());
        result.setFlKph(rec.getFlKph());
        result.setPosition(rec.getPosition());
        return result;
    }

    @Override
    protected Result insert(Result entity) {
        Record record = dsl.insertInto(table)
                .columns(
                        RESULTS.SESSION_ID,
                        RESULTS.CAR_ENTRY_ID,
                        RESULTS.CAR_NUMBER,
                        RESULTS.TIRES,
                        RESULTS.STATUS,
                        RESULTS.LAPS,
                        RESULTS.TOTAL_TIME,
                        RESULTS.GAP_FIRST,
                        RESULTS.GAP_PREVIOUS,
                        RESULTS.FL_LAPNUM,
                        RESULTS.FL_TIME,
                        RESULTS.FL_KPH,
                        RESULTS.POSITION
                )
                .values(
                        entity.getSessionId(),
                        entity.getCarEntryId(),
                        entity.getCarNumber(),
                        entity.getTires(),
                        entity.getStatus(),
                        entity.getLaps(),
                        entity.getTotalTime(),
                        entity.getGapFirst(),
                        entity.getGapPrevious(),
                        entity.getFlLapnum(),
                        entity.getFlTime(),
                        entity.getFlKph(),
                        entity.getPosition()
                )
                .returning()
                .fetchOne();
        return mapToEntity(record);
    }

    @Override
    protected void update(Result entity) {
        dsl.update(table)
                .set(RESULTS.SESSION_ID, entity.getSessionId())
                .set(RESULTS.CAR_ENTRY_ID, entity.getCarEntryId())
                .set(RESULTS.CAR_NUMBER, entity.getCarNumber())
                .set(RESULTS.TIRES, entity.getTires())
                .set(RESULTS.STATUS, entity.getStatus())
                .set(RESULTS.LAPS, entity.getLaps())
                .set(RESULTS.TOTAL_TIME, entity.getTotalTime())
                .set(RESULTS.GAP_FIRST, entity.getGapFirst())
                .set(RESULTS.GAP_PREVIOUS, entity.getGapPrevious())
                .set(RESULTS.FL_LAPNUM, entity.getFlLapnum())
                .set(RESULTS.FL_TIME, entity.getFlTime())
                .set(RESULTS.FL_KPH, entity.getFlKph())
                .set(RESULTS.POSITION, entity.getPosition())
                .where(idField.eq(entity.getId()))
                .execute();
    }

    @Override
    public Optional<Result> findBySessionIdAndCarEntryId(Long sessionId, Long carEntryId) {
        Record record = dsl.select()
                .from(table)
                .where(RESULTS.SESSION_ID.eq(sessionId).and(RESULTS.CAR_ENTRY_ID.eq(carEntryId)))
                .fetchOne();
        return Optional.ofNullable(mapToEntity(record));
    }

    @Override
    public List<Result> findBySessionId(Long sessionId) {
        return dsl.select()
                .from(table)
                .where(RESULTS.SESSION_ID.eq(sessionId))
                .fetch()
                .map(this::mapToEntity);
    }

    @Override
    public List<Result> batchSave(List<Result> results) {
        if (results == null || results.isEmpty()) return List.of();
        var insertStep = dsl.insertInto(table)
                .columns(
                        RESULTS.SESSION_ID,
                        RESULTS.CAR_ENTRY_ID,
                        RESULTS.CAR_NUMBER,
                        RESULTS.TIRES,
                        RESULTS.STATUS,
                        RESULTS.LAPS,
                        RESULTS.TOTAL_TIME,
                        RESULTS.GAP_FIRST,
                        RESULTS.GAP_PREVIOUS,
                        RESULTS.FL_LAPNUM,
                        RESULTS.FL_TIME,
                        RESULTS.FL_KPH,
                        RESULTS.POSITION
                );
        for (Result entity : results) {
            insertStep = insertStep.values(
                    entity.getSessionId(),
                    entity.getCarEntryId(),
                    entity.getCarNumber(),
                    entity.getTires(),
                    entity.getStatus(),
                    entity.getLaps(),
                    entity.getTotalTime(),
                    entity.getGapFirst(),
                    entity.getGapPrevious(),
                    entity.getFlLapnum(),
                    entity.getFlTime(),
                    entity.getFlKph(),
                    entity.getPosition()
            );
        }
        var inserted = insertStep.returning().fetch();
        return inserted.map(this::mapToEntity);
    }
} 