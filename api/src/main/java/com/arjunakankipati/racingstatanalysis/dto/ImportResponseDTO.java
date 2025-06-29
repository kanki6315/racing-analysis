package com.arjunakankipati.racingstatanalysis.dto;

/**
 * Data Transfer Object for import responses.
 * Contains information about the status of an import operation.
 */
public class ImportResponseDTO {
    private String importId;
    private String status;
    private Long completionTime;
    private String error;
    private String url;
    private String importType;
    private String processType;
    private Long sessionId;

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
     * @param error the error message, if any
     */
    public ImportResponseDTO(String importId, String status, Long completionTime, String error) {
        this.importId = importId;
        this.status = status;
        this.completionTime = completionTime;
        this.error = error;
    }

    public ImportResponseDTO(String importId, String status, Long completionTime) {
        this(importId, status, completionTime, null);
    }

    public ImportResponseDTO(String importId, String status, Long completionTime, String error, String url, String importType, String processType, Long sessionId) {
        this.importId = importId;
        this.status = status;
        this.completionTime = completionTime;
        this.error = error;
        this.url = url;
        this.importType = importType;
        this.processType = processType;
        this.sessionId = sessionId;
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

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImportType() {
        return importType;
    }

    public void setImportType(String importType) {
        this.importType = importType;
    }

    public String getProcessType() {
        return processType;
    }

    public void setProcessType(String processType) {
        this.processType = processType;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }
}