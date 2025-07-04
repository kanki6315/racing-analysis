package com.arjunakankipati.racingstatanalysis.service.impl;

import com.arjunakankipati.racingstatanalysis.dto.ProcessRequestDTO;
import com.arjunakankipati.racingstatanalysis.model.ImportJob;
import com.arjunakankipati.racingstatanalysis.repository.ImportJobRepository;
import com.arjunakankipati.racingstatanalysis.service.ImportJobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ImportJobServiceImpl implements ImportJobService {
    private final ImportJobRepository importJobRepository;

    @Autowired
    public ImportJobServiceImpl(ImportJobRepository importJobRepository) {
        this.importJobRepository = importJobRepository;
    }

    @Override
    public ImportJob createJob(ProcessRequestDTO request) {
        ImportJob job = new ImportJob();
        job.setStatus("PENDING");
        job.setCreatedAt(LocalDateTime.now());
        job.setUpdatedAt(LocalDateTime.now());
        job.setUrl(request.getUrl());
        job.setSessionId(request.getSessionId());
        job.setImportType(request.getImportType().name());
        job.setProcessType(request.getProcessType().name());
        return importJobRepository.save(job);
    }

    @Override
    public void markStarted(Integer jobId) {
        importJobRepository.updateStatus(jobId, "IN_PROGRESS");
        importJobRepository.updateStartedAt(jobId);
    }

    @Override
    public void markCompleted(Integer jobId) {
        importJobRepository.updateEndedAtAndStatusAndError(jobId, "COMPLETED", null);
    }

    @Override
    public void markFailed(Integer jobId, String error) {
        importJobRepository.updateEndedAtAndStatusAndError(jobId, "FAILED", error);
    }

    @Override
    public Optional<ImportJob> getJob(Integer jobId) {
        return importJobRepository.findById(jobId);
    }

    @Override
    public List<ImportJob> getAllJobs() {
        return importJobRepository.findAll();
    }
} 