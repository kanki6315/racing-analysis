package com.arjunakankipati.racingstatanalysis.service;

import com.arjunakankipati.racingstatanalysis.dto.ImportRequestDTO;
import com.arjunakankipati.racingstatanalysis.dto.ImportResponseDTO;

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