package com.arjunakankipati.racingstatanalysis.service;

import com.arjunakankipati.racingstatanalysis.dto.ProcessRequestDTO;
import com.arjunakankipati.racingstatanalysis.model.ImportJob;

import java.util.List;
import java.util.Optional;

public interface ImportJobService {
    ImportJob createJob(ProcessRequestDTO request);

    void markStarted(Integer jobId);

    void markCompleted(Integer jobId);

    void markFailed(Integer jobId, String error);

    Optional<ImportJob> getJob(Integer jobId);

    List<ImportJob> getAllJobs();
} 