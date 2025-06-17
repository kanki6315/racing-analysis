package com.arjunakankipati.racing_stat_analysis.controller;

import com.arjunakankipati.racing_stat_analysis.dto.ImportRequestDTO;
import com.arjunakankipati.racing_stat_analysis.dto.ImportResponseDTO;
import com.arjunakankipati.racing_stat_analysis.service.ImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for import operations.
 */
@RestController
@RequestMapping("/api/v1/imports")
public class ImportController {

    private final ImportService importService;

    /**
     * Constructor with ImportService dependency injection.
     *
     * @param importService the import service
     */
    @Autowired
    public ImportController(ImportService importService) {
        this.importService = importService;
    }

    /**
     * Imports timing data from a URL.
     *
     * @param request the import request containing the URL and metadata
     * @return a response entity containing the import response
     */
    @PostMapping
    public ResponseEntity<ImportResponseDTO> importData(@RequestBody ImportRequestDTO request) {
        ImportResponseDTO result = importService.importFromUrl(request);
        return ResponseEntity.accepted().body(result);
    }
}
