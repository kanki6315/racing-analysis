package com.arjunakankipati.racingstatanalysis.service.impl;

import com.arjunakankipati.racingstatanalysis.dto.SeriesDTO;
import com.arjunakankipati.racingstatanalysis.dto.SeriesResponseDTO;
import com.arjunakankipati.racingstatanalysis.exceptions.ResourceExistsException;
import com.arjunakankipati.racingstatanalysis.model.Event;
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

    @Autowired
    private SeriesRepository seriesRepository;
    @Autowired
    private EventRepository eventRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<SeriesResponseDTO> findAll() {
        List<Series> seriesList = seriesRepository.findAll();

        return seriesList.stream()
                .map(series -> {
                    // Count events for this series
                    // TODO - make one query and return event count and years
                    var events = eventRepository.findBySeriesId(series.getId());
                    // Create DTO with series data, event count, and years
                    return new SeriesResponseDTO(
                            series.getId(),
                            series.getName(),
                            events.size(),
                            events.stream()
                                    .map(Event::getYear)
                                    .distinct()
                                    .toList()
                    );
                })
                .toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SeriesResponseDTO createSeries(SeriesDTO seriesDTO) {
        var existing = seriesRepository.findByName(seriesDTO.getName());
        if (existing.isPresent()) throw new ResourceExistsException();

        Series series = new Series();
        series.setName(seriesDTO.getName());
        series.setDescription(seriesDTO.getDescription());
        Series saved = seriesRepository.save(series);
        return new SeriesResponseDTO(saved.getId(), saved.getName(), 0, null);
    }
}
