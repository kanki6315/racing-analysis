package com.arjunakankipati.racingstatanalysis.service;

import com.arjunakankipati.racingstatanalysis.dto.SeriesDTO;
import com.arjunakankipati.racingstatanalysis.dto.SeriesResponseDTO;

import java.util.List;

/**
 * Service interface for series operations.
 */
public interface SeriesService {

    /**
     * Finds all series, including the years for each series.
     *
     * @return a list of all series with their years
     * @throws UnsupportedOperationException if the operation is not implemented
     */
    List<SeriesResponseDTO> findAll();

    /**
     * Creates a new series.
     *
     * @param seriesDTO the series data
     * @return the created series as a response DTO
     */
    SeriesResponseDTO createSeries(SeriesDTO seriesDTO);
}
