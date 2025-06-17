package com.arjunakankipati.racing_stat_analysis.controller;

import com.arjunakankipati.racing_stat_analysis.dto.SeriesResponseDTO;
import com.arjunakankipati.racing_stat_analysis.dto.YearsResponseDTO;
import com.arjunakankipati.racing_stat_analysis.service.SeriesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for series operations.
 */
@RestController
@RequestMapping("/api/v1/series")
public class SeriesController {

    private final SeriesService seriesService;

    /**
     * Constructor with SeriesService dependency injection.
     *
     * @param seriesService the series service
     */
    @Autowired
    public SeriesController(SeriesService seriesService) {
        this.seriesService = seriesService;
    }

    /**
     * Gets all series.
     *
     * @return a response entity containing a list of all series
     */
    @GetMapping
    public ResponseEntity<List<SeriesResponseDTO>> getAllSeries() {
        List<SeriesResponseDTO> series = seriesService.findAll();
        return ResponseEntity.ok(series);
    }

    /**
     * Gets all years for a specific series.
     *
     * @param seriesId the ID of the series
     * @return a response entity containing the years for the series
     */
    @GetMapping("/{seriesId}/years")
    public ResponseEntity<YearsResponseDTO> getYearsForSeries(@PathVariable Long seriesId) {
        YearsResponseDTO years = seriesService.findYearsBySeries(seriesId);
        return ResponseEntity.ok(years);
    }
}