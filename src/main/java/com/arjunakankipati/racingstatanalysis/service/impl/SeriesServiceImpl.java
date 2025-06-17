package com.arjunakankipati.racingstatanalysis.service.impl;

import com.arjunakankipati.racingstatanalysis.dto.SeriesResponseDTO;
import com.arjunakankipati.racingstatanalysis.dto.YearsResponseDTO;
import com.arjunakankipati.racingstatanalysis.service.SeriesService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementation of the SeriesService interface.
 */
@Service
public class SeriesServiceImpl implements SeriesService {

    /**
     * {@inheritDoc}
     */
    @Override
    public List<SeriesResponseDTO> findAll() {
        throw new UnsupportedOperationException("Method not implemented yet");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public YearsResponseDTO findYearsBySeries(Long seriesId) {
        throw new UnsupportedOperationException("Method not implemented yet");
    }
}