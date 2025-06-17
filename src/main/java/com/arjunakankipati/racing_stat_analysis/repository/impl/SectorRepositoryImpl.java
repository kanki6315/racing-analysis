package com.arjunakankipati.racing_stat_analysis.repository.impl;

import com.arjunakankipati.racing_stat_analysis.model.Sector;
import com.arjunakankipati.racing_stat_analysis.repository.SectorRepository;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.jooq.impl.DSL.field;

/**
 * Implementation of the SectorRepository interface using JOOQ.
 * Extends BaseRepositoryImpl to inherit common CRUD operations.
 */
@Repository
public class SectorRepositoryImpl extends BaseRepositoryImpl<Sector, Long> implements SectorRepository {

    /**
     * Constructor with DSLContext dependency injection.
     *
     * @param dsl the JOOQ DSL context
     */
    @Autowired
    public SectorRepositoryImpl(DSLContext dsl) {
        super(dsl, "sectors", "id", Long.class);
    }

    @Override
    protected Sector mapToEntity(Record record) {
        if (record == null) {
            return null;
        }

        return new Sector(
                record.get(field("id", Long.class)),
                record.get(field("lap_id", Long.class)),
                record.get(field("sector_number", Integer.class)),
                record.get(field("sector_time_seconds", BigDecimal.class)),
                record.get(field("is_personal_best", Boolean.class)),
                record.get(field("is_session_best", Boolean.class))
        );
    }

    @Override
    protected Sector insert(Sector sector) {
        Record record = dsl.insertInto(table)
                .columns(
                        field("lap_id"),
                        field("sector_number"),
                        field("sector_time_seconds"),
                        field("is_personal_best"),
                        field("is_session_best")
                )
                .values(
                        sector.getLapId(),
                        sector.getSectorNumber(),
                        sector.getSectorTimeSeconds(),
                        sector.getIsPersonalBest(),
                        sector.getIsSessionBest()
                )
                .returningResult(
                        field("id"),
                        field("lap_id"),
                        field("sector_number"),
                        field("sector_time_seconds"),
                        field("is_personal_best"),
                        field("is_session_best")
                )
                .fetchOne();

        return mapToEntity(record);
    }

    @Override
    protected void update(Sector sector) {
        dsl.update(table)
                .set(field("lap_id"), sector.getLapId())
                .set(field("sector_number"), sector.getSectorNumber())
                .set(field("sector_time_seconds"), sector.getSectorTimeSeconds())
                .set(field("is_personal_best"), sector.getIsPersonalBest())
                .set(field("is_session_best"), sector.getIsSessionBest())
                .where(idField.eq(sector.getId()))
                .execute();
    }

    @Override
    public List<Sector> findByLapId(Long lapId) {
        return dsl.select()
                .from(table)
                .where(field("lap_id").eq(lapId))
                .fetch()
                .map(this::mapToEntity);
    }

    @Override
    public Optional<Sector> findByLapIdAndSectorNumber(Long lapId, Integer sectorNumber) {
        Record record = dsl.select()
                .from(table)
                .where(field("lap_id").eq(lapId))
                .and(field("sector_number").eq(sectorNumber))
                .fetchOne();

        return Optional.ofNullable(record)
                .map(this::mapToEntity);
    }

    @Override
    public List<Sector> findByLapIdAndIsPersonalBestTrue(Long lapId) {
        return dsl.select()
                .from(table)
                .where(field("lap_id").eq(lapId))
                .and(field("is_personal_best").eq(true))
                .fetch()
                .map(this::mapToEntity);
    }

    @Override
    public List<Sector> findByLapIdAndIsSessionBestTrue(Long lapId) {
        return dsl.select()
                .from(table)
                .where(field("lap_id").eq(lapId))
                .and(field("is_session_best").eq(true))
                .fetch()
                .map(this::mapToEntity);
    }

    @Override
    public List<Sector> findTopSectorsByLapId(Long lapId, int limit) {
        return dsl.select()
                .from(table)
                .where(field("lap_id").eq(lapId))
                .orderBy(field("sector_time_seconds").asc())
                .limit(limit)
                .fetch()
                .map(this::mapToEntity);
    }
}