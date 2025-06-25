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
    private String sourceUrl;

    public ImportJob() {
    }

    public ImportJob(Integer id, String status, LocalDateTime createdAt, LocalDateTime updatedAt,
                     LocalDateTime startedAt, LocalDateTime endedAt, String error, String sourceUrl) {
        this.id = id;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.startedAt = startedAt;
        this.endedAt = endedAt;
        this.error = error;
        this.sourceUrl = sourceUrl;
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

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
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
                ", sourceUrl='" + sourceUrl + '\'' +
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
                Objects.equals(sourceUrl, importJob.sourceUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, status, createdAt, updatedAt, startedAt, endedAt, error, sourceUrl);
    }
} 