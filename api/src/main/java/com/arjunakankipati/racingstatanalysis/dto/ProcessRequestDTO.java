package com.arjunakankipati.racingstatanalysis.dto;

public class ProcessRequestDTO {
    private String url;
    private Long sessionId;
    private ImportType importType;
    private ProcessType processType;

    public enum ImportType {
        IMSA, WEC
    }

    public enum ProcessType {
        RESULTS, TIMECARD
    }

    public ProcessRequestDTO() {
    }

    public ProcessRequestDTO(String url, Long sessionId, ImportType importType, ProcessType processType) {
        this.url = url;
        this.sessionId = sessionId;
        this.importType = importType;
        this.processType = processType;
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

    public ProcessType getProcessType() {
        return processType;
    }

    public void setProcessType(ProcessType processType) {
        this.processType = processType;
    }
}