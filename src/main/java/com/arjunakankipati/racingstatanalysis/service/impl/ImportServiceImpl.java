package com.arjunakankipati.racingstatanalysis.service.impl;

import com.arjunakankipati.racingstatanalysis.dto.ImportRequestDTO;
import com.arjunakankipati.racingstatanalysis.dto.ImportResponseDTO;
import com.arjunakankipati.racingstatanalysis.service.ImportService;
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