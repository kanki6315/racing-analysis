package com.arjunakankipati.racingstatanalysis.service;

import com.arjunakankipati.racingstatanalysis.dto.ClassesResponseDTO;
import com.arjunakankipati.racingstatanalysis.dto.EventsResponseDTO;
import com.arjunakankipati.racingstatanalysis.dto.SeriesResponseDTO;
import com.arjunakankipati.racingstatanalysis.dto.TeamsResponseDTO;
import com.arjunakankipati.racingstatanalysis.dto.YearsResponseDTO;

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

    List<EventsResponseDTO> findEventsForSeriesByYear(Long seriesId, Integer year);

    /**
     * Finds all teams that participated in a specific event.
     *
     * @param eventId the ID of the event
     * @return a response containing the teams that participated in the event
     * @throws com.arjunakankipati.racingstatanalysis.exceptions.ResourceNotFoundException if the event is not found
     */
    TeamsResponseDTO findTeamsByEventId(Long eventId);

    /**
     * Finds all classes that competed in a specific event.
     *
     * @param eventId the ID of the event
     * @return a response containing the classes that competed in the event
     * @throws com.arjunakankipati.racingstatanalysis.exceptions.ResourceNotFoundException if the event is not found
     */
    ClassesResponseDTO findClassesByEventId(Long eventId);
}
