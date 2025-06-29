package com.arjunakankipati.racingstatanalysis.repository;

import com.arjunakankipati.racingstatanalysis.model.ImportJob;

import java.util.List;
import java.util.Optional;

public interface ImportJobRepository extends BaseRepository<ImportJob, Integer> {
    Optional<ImportJob> findById(Integer id);

    ImportJob save(ImportJob job);

    void updateStatus(Integer id, String status);

    void updateStartedAt(Integer id);

    void updateEndedAtAndStatusAndError(Integer id, String status, String error);

    List<ImportJob> findAll();
    // Add more specific methods as needed
} 