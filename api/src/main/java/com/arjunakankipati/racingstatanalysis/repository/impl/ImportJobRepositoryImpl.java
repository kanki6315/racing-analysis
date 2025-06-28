package com.arjunakankipati.racingstatanalysis.repository.impl;

import com.arjunakankipati.racingstatanalysis.jooq.Tables;
import com.arjunakankipati.racingstatanalysis.jooq.tables.records.ImportJobsRecord;
import com.arjunakankipati.racingstatanalysis.model.ImportJob;
import com.arjunakankipati.racingstatanalysis.repository.ImportJobRepository;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public class ImportJobRepositoryImpl implements ImportJobRepository {
    private final DSLContext dsl;

    @Autowired
    public ImportJobRepositoryImpl(DSLContext dsl) {
        this.dsl = dsl;
    }

    private ImportJob mapToEntity(Record record) {
        if (record == null) return null;
        ImportJobsRecord r = record.into(Tables.IMPORT_JOBS);
        return new ImportJob(
                r.getId(),
                r.getStatus(),
                r.getCreatedAt(),
                r.getUpdatedAt(),
                r.getStartedAt(),
                r.getEndedAt(),
                r.getError(),
                r.getUrl(),
                r.getImportType(),
                r.getProcessType(),
                r.getSessionId()
        );
    }

    @Override
    public Optional<ImportJob> findById(Integer id) {
        Record record = dsl.select()
                .from(Tables.IMPORT_JOBS)
                .where(Tables.IMPORT_JOBS.ID.eq(id))
                .fetchOne();
        return Optional.ofNullable(mapToEntity(record));
    }

    @Override
    public ImportJob save(ImportJob job) {
        if (job.getId() == null) {
            // Insert
            ImportJobsRecord record = dsl.insertInto(Tables.IMPORT_JOBS)
                    .set(Tables.IMPORT_JOBS.STATUS, job.getStatus())
                    .set(Tables.IMPORT_JOBS.CREATED_AT, job.getCreatedAt() != null ? job.getCreatedAt() : LocalDateTime.now())
                    .set(Tables.IMPORT_JOBS.UPDATED_AT, job.getUpdatedAt() != null ? job.getUpdatedAt() : LocalDateTime.now())
                    .set(Tables.IMPORT_JOBS.STARTED_AT, job.getStartedAt())
                    .set(Tables.IMPORT_JOBS.ENDED_AT, job.getEndedAt())
                    .set(Tables.IMPORT_JOBS.ERROR, job.getError())
                    .set(Tables.IMPORT_JOBS.URL, job.getUrl())
                    .set(Tables.IMPORT_JOBS.PROCESS_TYPE, job.getProcessType())
                    .set(Tables.IMPORT_JOBS.IMPORT_TYPE, job.getImportType())
                    .set(Tables.IMPORT_JOBS.SESSION_ID, job.getSessionId())
                    .returning()
                    .fetchOne();
            return mapToEntity(record);
        } else {
            // Update
            dsl.update(Tables.IMPORT_JOBS)
                    .set(Tables.IMPORT_JOBS.STATUS, job.getStatus())
                    .set(Tables.IMPORT_JOBS.UPDATED_AT, LocalDateTime.now())
                    .set(Tables.IMPORT_JOBS.STARTED_AT, job.getStartedAt())
                    .set(Tables.IMPORT_JOBS.ENDED_AT, job.getEndedAt())
                    .set(Tables.IMPORT_JOBS.ERROR, job.getError())
                    .set(Tables.IMPORT_JOBS.URL, job.getUrl())
                    .set(Tables.IMPORT_JOBS.PROCESS_TYPE, job.getProcessType())
                    .set(Tables.IMPORT_JOBS.IMPORT_TYPE, job.getImportType())
                    .set(Tables.IMPORT_JOBS.SESSION_ID, job.getSessionId())
                    .where(Tables.IMPORT_JOBS.ID.eq(job.getId()))
                    .execute();
            return findById(job.getId()).orElse(null);
        }
    }

    @Override
    public void updateStatus(Integer id, String status) {
        dsl.update(Tables.IMPORT_JOBS)
                .set(Tables.IMPORT_JOBS.STATUS, status)
                .set(Tables.IMPORT_JOBS.UPDATED_AT, LocalDateTime.now())
                .where(Tables.IMPORT_JOBS.ID.eq(id))
                .execute();
    }

    @Override
    public void updateStartedAt(Integer id) {
        dsl.update(Tables.IMPORT_JOBS)
                .set(Tables.IMPORT_JOBS.STARTED_AT, LocalDateTime.now())
                .set(Tables.IMPORT_JOBS.UPDATED_AT, LocalDateTime.now())
                .where(Tables.IMPORT_JOBS.ID.eq(id))
                .execute();
    }

    @Override
    public void updateEndedAtAndStatusAndError(Integer id, String status, String error) {
        dsl.update(Tables.IMPORT_JOBS)
                .set(Tables.IMPORT_JOBS.ENDED_AT, LocalDateTime.now())
                .set(Tables.IMPORT_JOBS.STATUS, status)
                .set(Tables.IMPORT_JOBS.ERROR, error)
                .set(Tables.IMPORT_JOBS.UPDATED_AT, LocalDateTime.now())
                .where(Tables.IMPORT_JOBS.ID.eq(id))
                .execute();
    }

    @Override
    public java.util.List<ImportJob> findAll() {
        return dsl.selectFrom(Tables.IMPORT_JOBS)
                .fetch()
                .map(this::mapToEntity);
    }

    @Override
    public boolean deleteById(Integer id) {
        int rows = dsl.deleteFrom(Tables.IMPORT_JOBS)
                .where(Tables.IMPORT_JOBS.ID.eq(id))
                .execute();
        return rows > 0;
    }

    @Override
    public long count() {
        return dsl.fetchCount(Tables.IMPORT_JOBS);
    }
}