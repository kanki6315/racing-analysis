package com.arjunakankipati.racingstatanalysis.service;

import com.arjunakankipati.racingstatanalysis.dto.*;

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

    /**
     * Finds all cars in a specific class for a specific event.
     *
     * @param eventId the ID of the event
     * @param classId the ID of the class
     * @return a response containing the cars in the specified class for the event
     * @throws com.arjunakankipati.racingstatanalysis.exceptions.ResourceNotFoundException if the event or class is not found
     */
    CarsResponseDTO findCarsByEventIdAndClassId(Long eventId, Long classId);

    /**
     * Finds all car models in a specific class for a specific event.
     *
     * @param eventId the ID of the event
     * @param classId the ID of the class
     * @return a response containing the car models in the specified class for the event
     * @throws com.arjunakankipati.racingstatanalysis.exceptions.ResourceNotFoundException if the event or class is not found
     */
    CarModelsResponseDTO findCarModelsByEventIdAndClassId(Long eventId, Long classId);

    /**
     * Finds all drivers for a specific event with optional filters.
     *
     * @param eventId    the ID of the event
     * @param carModelId optional filter by car model ID
     * @param classId    optional filter by class ID
     * @return a response containing the drivers for the event
     * @throws com.arjunakankipati.racingstatanalysis.exceptions.ResourceNotFoundException if the event is not found
     */
    DriversResponseDTO findDriversByEventId(Long eventId, Long carModelId, Long classId);

    /**
     * Creates a new series.
     *
     * @param seriesDTO the series data
     * @return the created series as a response DTO
     */
    SeriesResponseDTO createSeries(SeriesDTO seriesDTO);
}
