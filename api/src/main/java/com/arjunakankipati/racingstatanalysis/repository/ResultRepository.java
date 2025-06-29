package com.arjunakankipati.racingstatanalysis.repository;

import com.arjunakankipati.racingstatanalysis.model.Result;

import java.util.List;
import java.util.Optional;

public interface ResultRepository extends BaseRepository<Result, Long> {
    Optional<Result> findBySessionIdAndCarEntryId(Long sessionId, Long carEntryId);

    List<Result> findBySessionId(Long sessionId);

    List<Result> batchSave(List<Result> results);
    // Add more custom queries as needed
} 