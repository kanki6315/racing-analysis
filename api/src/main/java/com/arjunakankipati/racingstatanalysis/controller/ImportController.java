package com.arjunakankipati.racingstatanalysis.controller;

import com.arjunakankipati.racingstatanalysis.dto.ImportRequestDTO;
import com.arjunakankipati.racingstatanalysis.dto.ImportResponseDTO;
import com.arjunakankipati.racingstatanalysis.dto.ProcessResultsRequestDTO;
import com.arjunakankipati.racingstatanalysis.dto.ProcessResultsResponseDTO;
import com.arjunakankipati.racingstatanalysis.model.ImportJob;
import com.arjunakankipati.racingstatanalysis.service.ImportJobService;
import com.arjunakankipati.racingstatanalysis.service.ImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * REST controller for import operations.
 */
@RestController
@RequestMapping("/api/v1/imports")
public class ImportController {

    private final ImportService importService;
    private final ImportJobService importJobService;

    @Value("${api.key}")
    private String expectedApiKey;

    /**
     * Constructor with ImportService and ImportJobService dependency injection.
     */
    @Autowired
    public ImportController(ImportService importService, ImportJobService importJobService) {
        this.importService = importService;
        this.importJobService = importJobService;
    }

    /**
     * Starts an async import and returns the job ID.
     */
    @PostMapping
    public ResponseEntity<ImportResponseDTO> importData(
            @RequestBody ImportRequestDTO request,
            @RequestHeader(value = "X-API-Key", required = false) String apiKey) {
        if (apiKey == null || !apiKey.equals(expectedApiKey)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Integer jobId = importJobService.createJob(request.getUrl()).getId();
        importService.processImport(jobId, request); // This runs asynchronously!
        ImportResponseDTO response = new ImportResponseDTO(
                jobId != null ? jobId.toString() : null,
                "PENDING",
                null
        );
        return ResponseEntity.accepted().body(response);
    }

    /**
     * Gets the status of an import job by job ID.
     */
    @GetMapping("/status/{jobId}")
    public ResponseEntity<ImportResponseDTO> getImportStatus(@PathVariable Integer jobId) {
        Optional<ImportJob> jobOpt = importJobService.getJob(jobId);
        if (jobOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        ImportJob job = jobOpt.get();
        ImportResponseDTO response = new ImportResponseDTO(
                job.getId() != null ? job.getId().toString() : null,
                job.getStatus(),
                job.getEndedAt() != null && job.getStartedAt() != null ?
                        java.time.Duration.between(job.getStartedAt(), job.getEndedAt()).toMillis() : null,
                job.getError()
        );
        return ResponseEntity.ok(response);
    }

    /**
     * Processes a results CSV to ensure all required records exist before timecard import.
     */
    @PostMapping("/process-results")
    public ResponseEntity<ProcessResultsResponseDTO> processResults(
            @RequestBody ProcessResultsRequestDTO request,
            @RequestHeader(value = "X-API-Key", required = false) String apiKey) {
        if (apiKey == null || !apiKey.equals(expectedApiKey)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            // This should call a new service method to process the results CSV and create/ensure all required records
            ProcessResultsResponseDTO response = importService.processResultsCsv(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ProcessResultsResponseDTO(null, "FAILED", e.getMessage()));
        }
    }
}
