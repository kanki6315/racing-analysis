package com.arjunakankipati.racingstatanalysis.dto;

public class ProcessResponseDTO {
    private Long sessionId;
    private String status;
    private String error;

    public ProcessResponseDTO() {
    }

    public ProcessResponseDTO(Long sessionId, String status, String error) {
        this.sessionId = sessionId;
        this.status = status;
        this.error = error;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
} 