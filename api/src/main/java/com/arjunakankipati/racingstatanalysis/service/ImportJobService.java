package com.arjunakankipati.racingstatanalysis.service;

import com.arjunakankipati.racingstatanalysis.model.ImportJob;

import java.util.Optional;

public interface ImportJobService {
    ImportJob createJob(String sourceUrl);

    void markStarted(Integer jobId);

    void markCompleted(Integer jobId);

    void markFailed(Integer jobId, String error);

    Optional<ImportJob> getJob(Integer jobId);
} 