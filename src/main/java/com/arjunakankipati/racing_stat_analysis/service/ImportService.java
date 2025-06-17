package com.arjunakankipati.racing_stat_analysis.service;

import com.arjunakankipati.racing_stat_analysis.dto.ImportRequestDTO;
import com.arjunakankipati.racing_stat_analysis.dto.ImportResponseDTO;

/**
 * Service interface for importing timing data.
 */
public interface ImportService {

    /**
     * Imports timing data from a URL.
     *
     * @param request the import request containing the URL and metadata
     * @return the import response containing the status of the import operation
     * @throws UnsupportedOperationException if the operation is not implemented
     */
    ImportResponseDTO importFromUrl(ImportRequestDTO request);
}