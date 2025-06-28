package com.arjunakankipati.racingstatanalysis.dto;

public class ProcessRequestDTO {
    private String url;
    private Long sessionId;

    public enum ImportType {
        IMSA, WEC
    }

    private ImportType importType;

    public ProcessRequestDTO() {
    }

    public ProcessRequestDTO(String url, Long sessionId, ImportType importType) {
        this.url = url;
        this.sessionId = sessionId;
        this.importType = importType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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