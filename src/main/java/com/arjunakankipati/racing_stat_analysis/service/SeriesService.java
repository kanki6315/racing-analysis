package com.arjunakankipati.racing_stat_analysis.service;

import com.arjunakankipati.racing_stat_analysis.dto.SeriesResponseDTO;
import com.arjunakankipati.racing_stat_analysis.dto.YearsResponseDTO;

import java.util.List;

/**
 * Service interface for series operations.
 */
public interface SeriesService {

    /**
     * Finds all series.
     *
     * @return a list of all series
     * @throws UnsupportedOperationException if the operation is not implemented
     */
    List<SeriesResponseDTO> findAll();

    /**
     * Finds all years for a specific series.
     *
     * @param seriesId the ID of the series
     * @return a response containing the years for the series
     * @throws UnsupportedOperationException if the operation is not implemented
     */
    YearsResponseDTO findYearsBySeries(Long seriesId);
}