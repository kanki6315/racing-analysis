package com.arjunakankipati.racingstatanalysis.dto;

import java.time.Instant;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for import responses.
 * Contains information about the status of an import operation.
 */
public class ImportResponseDTO {
    private String importId;
    private String status;
    private Long completionTime;

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
     * @param completionTime the completion time of the import operation
     */
    public ImportResponseDTO(String importId, String status, Long completionTime) {
        this.importId = importId;
        this.status = status;
        this.completionTime = completionTime;
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

    public Long getCompletionTime() {
        return completionTime;
    }

    public void setCompletionTime(Long completionTime) {
        this.completionTime = completionTime;
    }
}