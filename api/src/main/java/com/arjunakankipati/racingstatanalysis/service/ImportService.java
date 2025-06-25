package com.arjunakankipati.racingstatanalysis.service;

import com.arjunakankipati.racingstatanalysis.dto.ImportRequestDTO;
import org.springframework.scheduling.annotation.Async;

/**
 * Service interface for importing timing data.
 */
public interface ImportService {

    /**
     * Processes the import for a given job ID (called asynchronously).
     * @param jobId the import job ID
     * @param request the import request
     */
    @Async
    void processImport(Integer jobId, ImportRequestDTO request);
}
