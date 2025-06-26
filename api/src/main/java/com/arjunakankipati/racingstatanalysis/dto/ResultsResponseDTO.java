package com.arjunakankipati.racingstatanalysis.dto;

import java.util.List;

public class ResultsResponseDTO {
    private Long sessionId;
    private List<ResultDTO> results;

    public ResultsResponseDTO(Long sessionId, List<ResultDTO> results) {
        this.sessionId = sessionId;
        this.results = results;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public List<ResultDTO> getResults() {
        return results;
    }

    public void setResults(List<ResultDTO> results) {
        this.results = results;
    }
} 