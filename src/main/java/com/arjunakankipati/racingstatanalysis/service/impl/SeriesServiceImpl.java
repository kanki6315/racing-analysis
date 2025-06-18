package com.arjunakankipati.racingstatanalysis.service.impl;

import com.arjunakankipati.racingstatanalysis.dto.EventsResponseDTO;
import com.arjunakankipati.racingstatanalysis.dto.SeriesResponseDTO;
import com.arjunakankipati.racingstatanalysis.dto.YearsResponseDTO;
import com.arjunakankipati.racingstatanalysis.exceptions.ResourceNotFoundException;
import com.arjunakankipati.racingstatanalysis.model.Series;
import com.arjunakankipati.racingstatanalysis.repository.EventRepository;
import com.arjunakankipati.racingstatanalysis.repository.SeriesRepository;
import com.arjunakankipati.racingstatanalysis.service.SeriesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementation of the SeriesService interface.
 */
@Service
public class SeriesServiceImpl implements SeriesService {

    private final SeriesRepository seriesRepository;
    private final EventRepository eventRepository;

    /**
     * Constructor with repository dependency injection.
     *
     * @param seriesRepository the series repository
     * @param eventRepository  the event repository
     */
    @Autowired
    public SeriesServiceImpl(SeriesRepository seriesRepository, EventRepository eventRepository) {
        this.seriesRepository = seriesRepository;
        this.eventRepository = eventRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<SeriesResponseDTO> findAll() {
        List<Series> seriesList = seriesRepository.findAll();

        return seriesList.stream()
                .map(series -> {
                    // Count events for this series
                    long eventCount = eventRepository.findBySeriesId(series.getId()).size();

                    // Create DTO with series data and event count
                    return new SeriesResponseDTO(
                            series.getId(),
                            series.getName(),
                            (int) eventCount
                    );
                })
                .toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public YearsResponseDTO findYearsBySeries(Long seriesId) {
        // Find the series by ID
        Series series = seriesRepository.findById(seriesId)
                .orElseThrow(ResourceNotFoundException::new);

        // Get years for this series
        List<Integer> years = eventRepository.findYearsBySeriesId(seriesId);

        // Create response DTO
        return new YearsResponseDTO(
                series.getId(),
                series.getName(),
                years
        );
    }

    @Override
    public List<EventsResponseDTO> findEventsForSeriesByYear(Long seriesId, Integer year) {
        // Find the series by ID
        Series series = seriesRepository.findById(seriesId)
                .orElseThrow(ResourceNotFoundException::new);

        var events = eventRepository.findBySeriesIdAndYear(seriesId, year);

        return events.stream().map(event -> new EventsResponseDTO(
                        event.getId(),
                        event.getSeriesId(),
                        event.getName(),
                        event.getYear(),
                        event.getStartDate(),
                        event.getEndDate(),
                        event.getDescription()))
                .toList();
    }
}
