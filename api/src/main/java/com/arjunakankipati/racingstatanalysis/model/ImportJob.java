package com.arjunakankipati.racingstatanalysis.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class ImportJob implements BaseEntity<Integer> {
    private Integer id;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private String error;
    private String url;
    private String importType;
    private String processType;
    private Long sessionId;

    public ImportJob() {
    }

    public ImportJob(Integer id, String status, LocalDateTime createdAt, LocalDateTime updatedAt,
                     LocalDateTime startedAt, LocalDateTime endedAt, String error, String url, String importType, String processType, Long sessionId) {
        this.id = id;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.startedAt = startedAt;
        this.endedAt = endedAt;
        this.error = error;
        this.url = url;
        this.importType = importType;
        this.processType = processType;
        this.sessionId = sessionId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public LocalDateTime getEndedAt() {
        return endedAt;
    }

    public void setEndedAt(LocalDateTime endedAt) {
        this.endedAt = endedAt;
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

    @Override
    public String toString() {
        return "ImportJob{" +
                "id=" + id +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", startedAt=" + startedAt +
                ", endedAt=" + endedAt +
                ", error='" + error + '\'' +
                ", url='" + url + '\'' +
                ", importType='" + importType + '\'' +
                ", processType='" + processType + '\'' +
                ", sessionId=" + sessionId +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImportJob importJob = (ImportJob) o;
        return Objects.equals(id, importJob.id) &&
                Objects.equals(status, importJob.status) &&
                Objects.equals(createdAt, importJob.createdAt) &&
                Objects.equals(updatedAt, importJob.updatedAt) &&
                Objects.equals(startedAt, importJob.startedAt) &&
                Objects.equals(endedAt, importJob.endedAt) &&
                Objects.equals(error, importJob.error) &&
                Objects.equals(url, importJob.url) &&
                Objects.equals(importType, importJob.importType) &&
                Objects.equals(processType, importJob.processType) &&
                Objects.equals(sessionId, importJob.sessionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, status, createdAt, updatedAt, startedAt, endedAt, error, url, importType, processType, sessionId);
    }
} 