package com.arjunakankipati.racingstatanalysis.service;

import com.arjunakankipati.racingstatanalysis.dto.ProcessRequestDTO;
import com.arjunakankipati.racingstatanalysis.dto.ProcessResponseDTO;
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
    void processImport(Integer jobId, ProcessRequestDTO request);

    /**
     * Processes a results CSV to ensure all required records exist before timecard import.
     *
     * @param request the process results request
     * @return the process results response
     */
    ProcessResponseDTO processResultsCsv(ProcessRequestDTO request);

    ProcessResponseDTO processTimecardCsv(ProcessRequestDTO request);
}
