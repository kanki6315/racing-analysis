package com.arjunakankipati.racingstatanalysis.dto;

public class ProcessResultsRequestDTO {
    private String resultsUrl;
    private Long sessionId;

    public enum ImportType {
        IMSA, WEC
    }

    private ImportType importType;

    public ProcessResultsRequestDTO() {
    }

    public ProcessResultsRequestDTO(String resultsUrl, Long sessionId, ImportType importType) {
        this.resultsUrl = resultsUrl;
        this.sessionId = sessionId;
        this.importType = importType;
    }

    public String getResultsUrl() {
        return resultsUrl;
    }

    public void setResultsUrl(String resultsUrl) {
        this.resultsUrl = resultsUrl;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public ImportType getImportType() {
        return importType;
    }

    public void setImportType(ImportType importType) {
        this.importType = importType;
    }
} 