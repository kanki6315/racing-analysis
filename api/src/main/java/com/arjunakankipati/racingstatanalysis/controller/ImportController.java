package com.arjunakankipati.racingstatanalysis.controller;

import com.arjunakankipati.racingstatanalysis.dto.ImportRequestDTO;
import com.arjunakankipati.racingstatanalysis.dto.ImportResponseDTO;
import com.arjunakankipati.racingstatanalysis.service.ImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for import operations.
 */
@RestController
@RequestMapping("/api/v1/imports")
public class ImportController {

    private final ImportService importService;

    @Value("${api.key}")
    private String expectedApiKey;

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
     * @param request the import request containing the URL
     * @param apiKey the API key from the request header
     * @return a response entity containing the import response
     */
    @PostMapping
    public ResponseEntity<ImportResponseDTO> importData(
            @RequestBody ImportRequestDTO request,
            @RequestHeader(value = "X-API-Key", required = false) String apiKey) {
        if (apiKey == null || !apiKey.equals(expectedApiKey)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        ImportResponseDTO result = importService.importFromUrl(request);
        return ResponseEntity.accepted().body(result);
    }
}
