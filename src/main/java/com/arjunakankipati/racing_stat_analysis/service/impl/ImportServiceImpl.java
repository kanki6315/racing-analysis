package com.arjunakankipati.racing_stat_analysis.service.impl;

import com.arjunakankipati.racing_stat_analysis.dto.ImportRequestDTO;
import com.arjunakankipati.racing_stat_analysis.dto.ImportResponseDTO;
import com.arjunakankipati.racing_stat_analysis.service.ImportService;
import org.springframework.stereotype.Service;

/**
 * Implementation of the ImportService interface.
 */
@Service
public class ImportServiceImpl implements ImportService {

    /**
     * {@inheritDoc}
     */
    @Override
    public ImportResponseDTO importFromUrl(ImportRequestDTO request) {
        throw new UnsupportedOperationException("Method not implemented yet");
    }
}