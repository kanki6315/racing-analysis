package com.arjunakankipati.racing_stat_analysis.service.impl;

import com.arjunakankipati.racing_stat_analysis.dto.SeriesResponseDTO;
import com.arjunakankipati.racing_stat_analysis.dto.YearsResponseDTO;
import com.arjunakankipati.racing_stat_analysis.service.SeriesService;
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