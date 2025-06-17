package com.arjunakankipati.racingstatanalysis.dto;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for import responses.
 * Contains information about the status of an import operation.
 */
public class ImportResponseDTO {
    private String importId;
    private String status;
    private LocalDateTime estimatedCompletionTime;

    /**
     * Default constructor.
     */
    public ImportResponseDTO() {
    }

    /**
     * Full constructor.
     *
     * @param importId the ID of the import operation
     * @param status the status of the import operation
     * @param estimatedCompletionTime the estimated completion time of the import operation
     */
    public ImportResponseDTO(String importId, String status, LocalDateTime estimatedCompletionTime) {
        this.importId = importId;
        this.status = status;
        this.estimatedCompletionTime = estimatedCompletionTime;
    }

    /**
     * Gets the ID of the import operation.
     *
     * @return the import ID
     */
    public String getImportId() {
        return importId;
    }

    /**
     * Sets the ID of the import operation.
     *
     * @param importId the import ID to set
     */
    public void setImportId(String importId) {
        this.importId = importId;
    }

    /**
     * Gets the status of the import operation.
     *
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the status of the import operation.
     *
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Gets the estimated completion time of the import operation.
     *
     * @return the estimated completion time
     */
    public LocalDateTime getEstimatedCompletionTime() {
        return estimatedCompletionTime;
    }

    /**
     * Sets the estimated completion time of the import operation.
     *
     * @param estimatedCompletionTime the estimated completion time to set
     */
    public void setEstimatedCompletionTime(LocalDateTime estimatedCompletionTime) {
        this.estimatedCompletionTime = estimatedCompletionTime;
    }
}