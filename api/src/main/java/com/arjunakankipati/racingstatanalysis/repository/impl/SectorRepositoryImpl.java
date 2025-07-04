package com.arjunakankipati.racingstatanalysis.repository.impl;

import com.arjunakankipati.racingstatanalysis.jooq.Tables;
import com.arjunakankipati.racingstatanalysis.model.Sector;
import com.arjunakankipati.racingstatanalysis.repository.SectorRepository;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


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
        super(dsl, Tables.SECTORS, Tables.SECTORS.ID);
    }

    @Override
    protected Sector mapToEntity(Record record) {
        if (record == null) {
            return null;
        }

        var secRec = record.into(Tables.SECTORS);
        return new Sector(
                secRec.getId(),
                secRec.getLapId(),
                secRec.getSectorNumber(),
                secRec.getSectorTimeSeconds(),
                secRec.getIsPersonalBest(),
                secRec.getIsSessionBest()
        );
    }

    @Override
    protected Sector insert(Sector sector) {
        Record record = dsl.insertInto(table)
                .columns(
                        Tables.SECTORS.LAP_ID,
                        Tables.SECTORS.SECTOR_NUMBER,
                        Tables.SECTORS.SECTOR_TIME_SECONDS,
                        Tables.SECTORS.IS_PERSONAL_BEST,
                        Tables.SECTORS.IS_SESSION_BEST
                )
                .values(
                        sector.getLapId(),
                        sector.getSectorNumber(),
                        sector.getSectorTimeSeconds(),
                        sector.getIsPersonalBest(),
                        sector.getIsSessionBest()
                )
                .returning()
                .fetchOne();

        return mapToEntity(record);
    }

    @Override
    protected void update(Sector sector) {
        dsl.update(table)
                .set(Tables.SECTORS.LAP_ID, sector.getLapId())
                .set(Tables.SECTORS.SECTOR_NUMBER, sector.getSectorNumber())
                .set(Tables.SECTORS.SECTOR_TIME_SECONDS, sector.getSectorTimeSeconds())
                .set(Tables.SECTORS.IS_PERSONAL_BEST, sector.getIsPersonalBest())
                .set(Tables.SECTORS.IS_SESSION_BEST, sector.getIsSessionBest())
                .where(idField.eq(sector.getId()))
                .execute();
    }

    @Override
    public List<Sector> findByLapId(Long lapId) {
        return dsl.select()
                .from(table)
                .where(Tables.SECTORS.LAP_ID.eq(lapId))
                .fetch()
                .map(this::mapToEntity);
    }

    @Override
    public Optional<Sector> findByLapIdAndSectorNumber(Long lapId, Integer sectorNumber) {
        Record record = dsl.select()
                .from(table)
                .where(Tables.SECTORS.LAP_ID.eq(lapId))
                .and(Tables.SECTORS.SECTOR_NUMBER.eq(sectorNumber))
                .fetchOne();

        return Optional.ofNullable(record)
                .map(this::mapToEntity);
    }

    @Override
    public List<Sector> findByLapIdAndIsPersonalBestTrue(Long lapId) {
        return dsl.select()
                .from(table)
                .where(Tables.SECTORS.LAP_ID.eq(lapId))
                .and(Tables.SECTORS.IS_PERSONAL_BEST.eq(true))
                .fetch()
                .map(this::mapToEntity);
    }

    @Override
    public List<Sector> findByLapIdAndIsSessionBestTrue(Long lapId) {
        return dsl.select()
                .from(table)
                .where(Tables.SECTORS.LAP_ID.eq(lapId))
                .and(Tables.SECTORS.IS_SESSION_BEST.eq(true))
                .fetch()
                .map(this::mapToEntity);
    }

    @Override
    public List<Sector> findTopSectorsByLapId(Long lapId, int limit) {
        return dsl.select()
                .from(table)
                .where(Tables.SECTORS.LAP_ID.eq(lapId))
                .orderBy(Tables.SECTORS.SECTOR_TIME_SECONDS.asc())
                .limit(limit)
                .fetch()
                .map(this::mapToEntity);
    }

    @Override
    public List<Sector> saveAll(List<Sector> sectors) {
        if (sectors == null || sectors.isEmpty()) return new ArrayList<>();
        var insertStep = dsl.insertInto(table)
            .columns(
                Tables.SECTORS.LAP_ID,
                Tables.SECTORS.SECTOR_NUMBER,
                Tables.SECTORS.SECTOR_TIME_SECONDS,
                Tables.SECTORS.IS_PERSONAL_BEST,
                    Tables.SECTORS.IS_SESSION_BEST
            );
        for (Sector sector : sectors) {
            insertStep = insertStep.values(
                sector.getLapId(),
                sector.getSectorNumber(),
                sector.getSectorTimeSeconds(),
                sector.getIsPersonalBest(),
                    sector.getIsSessionBest()
            );
        }
        var result = insertStep.returning().fetch();
        List<Sector> savedSectors = new ArrayList<>();
        for (Record record : result) {
            savedSectors.add(mapToEntity(record));
        }
        return savedSectors;
    }
}
