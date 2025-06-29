package com.arjunakankipati.racingstatanalysis.controller;

import com.arjunakankipati.racingstatanalysis.dto.SeriesDTO;
import com.arjunakankipati.racingstatanalysis.dto.SeriesResponseDTO;
import com.arjunakankipati.racingstatanalysis.service.SeriesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for series operations.
 */
@RestController
@RequestMapping("/api/v1/series")
public class SeriesController {

    @Autowired
    private SeriesService seriesService;

    @Value("${api.key}")
    private String expectedApiKey;

    /**
     * Gets all series, including the years for each series.
     *
     * @return a response entity containing a list of all series with their years
     */
    @GetMapping
    public ResponseEntity<List<SeriesResponseDTO>> getAllSeries() {
        List<SeriesResponseDTO> series = seriesService.findAll();
        return ResponseEntity.ok(series);
    }

    @PostMapping
    public ResponseEntity<SeriesResponseDTO> createSeries(
            @RequestBody SeriesDTO seriesDTO,
            @RequestHeader(value = "X-API-Key", required = false) String apiKey) {
        if (apiKey == null || !apiKey.equals(expectedApiKey)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        SeriesResponseDTO created = seriesService.createSeries(seriesDTO);
        return ResponseEntity.ok(created);
    }
}
