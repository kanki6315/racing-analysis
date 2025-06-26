package com.arjunakankipati.racingstatanalysis.dto;

import java.time.LocalDateTime;

public class ProcessResultsRequestDTO {
    private String resultsUrl;
    private Long seriesId;
    private Long eventId;
    private String sessionName;
    private String sessionType;
    private LocalDateTime sessionDate;
    private Integer sessionDuration;
    private Long circuitId;

    public enum ImporterType {
        IMSA, WEC
    }

    private ImporterType importerType;

    public ProcessResultsRequestDTO() {
    }

    public ProcessResultsRequestDTO(String resultsUrl, Long seriesId, Long eventId, String sessionName, String sessionType, LocalDateTime sessionDate, Integer sessionDuration, Long circuitId, ImporterType importerType) {
        this.resultsUrl = resultsUrl;
        this.seriesId = seriesId;
        this.eventId = eventId;
        this.sessionName = sessionName;
        this.sessionType = sessionType;
        this.sessionDate = sessionDate;
        this.sessionDuration = sessionDuration;
        this.circuitId = circuitId;
        this.importerType = importerType;
    }

    public String getResultsUrl() {
        return resultsUrl;
    }

    public void setResultsUrl(String resultsUrl) {
        this.resultsUrl = resultsUrl;
    }

    public Long getSeriesId() {
        return seriesId;
    }

    public void setSeriesId(Long seriesId) {
        this.seriesId = seriesId;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getSessionName() {
        return sessionName;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    public String getSessionType() {
        return sessionType;
    }

    public void setSessionType(String sessionType) {
        this.sessionType = sessionType;
    }

    public LocalDateTime getSessionDate() {
        return sessionDate;
    }

    public void setSessionDate(LocalDateTime sessionDate) {
        this.sessionDate = sessionDate;
    }

    public Integer getSessionDuration() {
        return sessionDuration;
    }

    public void setSessionDuration(Integer sessionDuration) {
        this.sessionDuration = sessionDuration;
    }

    public Long getCircuitId() {
        return circuitId;
    }

    public void setCircuitId(Long circuitId) {
        this.circuitId = circuitId;
    }

    public ImporterType getImporterType() {
        return importerType;
    }

    public void setImporterType(ImporterType importerType) {
        this.importerType = importerType;
    }
} 