package com.arjunakankipati.racingstatanalysis.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a racing session (e.g., "Race", "Practice", "Qualifying").
 * A session belongs to an event and takes place at a specific circuit.
 */
public class Session implements BaseEntity<Long> {
    private Long id;
    private Long eventId;
    private Long circuitId;
    private String name;
    private String type;
    private LocalDateTime startDatetime;
    private Integer durationSeconds;
    private String importUrl;
    private LocalDateTime importTimestamp;

    /**
     * Default constructor.
     */
    public Session() {
    }

    /**
     * Full constructor.
     *
     * @param id the ID of the session
     * @param eventId the ID of the event this session belongs to
     * @param circuitId the ID of the circuit where this session takes place
     * @param name the name of the session
     * @param type the type of the session (e.g., "Race", "Practice", "Qualifying")
     * @param startDatetime the start date and time of the session
     * @param durationSeconds the duration of the session in seconds
     * @param importUrl the URL from which the session data was imported
     * @param importTimestamp the timestamp when the session data was imported
     */
    public Session(Long id, Long eventId, Long circuitId, String name, String type, LocalDateTime startDatetime,
                   Integer durationSeconds, String importUrl, LocalDateTime importTimestamp) {
        this.id = id;
        this.eventId = eventId;
        this.circuitId = circuitId;
        this.name = name;
        this.type = type;
        this.startDatetime = startDatetime;
        this.durationSeconds = durationSeconds;
        this.importUrl = importUrl;
        this.importTimestamp = importTimestamp;
    }

    /**
     * Gets the ID of the session.
     *
     * @return the ID
     */
    @Override
    public Long getId() {
        return id;
    }

    /**
     * Sets the ID of the session.
     *
     * @param id the ID to set
     */
    @Override
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the ID of the event this session belongs to.
     *
     * @return the event ID
     */
    public Long getEventId() {
        return eventId;
    }

    /**
     * Sets the ID of the event this session belongs to.
     *
     * @param eventId the event ID to set
     */
    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    /**
     * Gets the ID of the circuit where this session takes place.
     *
     * @return the circuit ID
     */
    public Long getCircuitId() {
        return circuitId;
    }

    /**
     * Sets the ID of the circuit where this session takes place.
     *
     * @param circuitId the circuit ID to set
     */
    public void setCircuitId(Long circuitId) {
        this.circuitId = circuitId;
    }

    /**
     * Gets the name of the session.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the session.
     *
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the type of the session.
     *
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the type of the session.
     *
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Gets the start date and time of the session.
     *
     * @return the start date and time
     */
    public LocalDateTime getStartDatetime() {
        return startDatetime;
    }

    /**
     * Sets the start date and time of the session.
     *
     * @param startDatetime the start date and time to set
     */
    public void setStartDatetime(LocalDateTime startDatetime) {
        this.startDatetime = startDatetime;
    }

    /**
     * Gets the duration of the session in seconds.
     *
     * @return the duration in seconds
     */
    public Integer getDurationSeconds() {
        return durationSeconds;
    }

    /**
     * Sets the duration of the session in seconds.
     *
     * @param durationSeconds the duration in seconds to set
     */
    public void setDurationSeconds(Integer durationSeconds) {
        this.durationSeconds = durationSeconds;
    }

    /**
     * Gets the URL from which the session data was imported.
     *
     * @return the import URL
     */
    public String getImportUrl() {
        return importUrl;
    }

    /**
     * Sets the URL from which the session data was imported.
     *
     * @param importUrl the import URL to set
     */
    public void setImportUrl(String importUrl) {
        this.importUrl = importUrl;
    }

    /**
     * Gets the timestamp when the session data was imported.
     *
     * @return the import timestamp
     */
    public LocalDateTime getImportTimestamp() {
        return importTimestamp;
    }

    /**
     * Sets the timestamp when the session data was imported.
     *
     * @param importTimestamp the import timestamp to set
     */
    public void setImportTimestamp(LocalDateTime importTimestamp) {
        this.importTimestamp = importTimestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Session session = (Session) o;
        return Objects.equals(id, session.id) &&
                Objects.equals(eventId, session.eventId) &&
                Objects.equals(circuitId, session.circuitId) &&
                Objects.equals(name, session.name) &&
                Objects.equals(type, session.type) &&
                Objects.equals(startDatetime, session.startDatetime) &&
                Objects.equals(durationSeconds, session.durationSeconds) &&
                Objects.equals(importUrl, session.importUrl) &&
                Objects.equals(importTimestamp, session.importTimestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, eventId, circuitId, name, type, startDatetime, durationSeconds, importUrl, importTimestamp);
    }

    @Override
    public String toString() {
        return "Session{" +
                "id=" + id +
                ", eventId=" + eventId +
                ", circuitId=" + circuitId +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", startDatetime=" + startDatetime +
                ", durationSeconds=" + durationSeconds +
                ", importUrl='" + importUrl + '\'' +
                ", importTimestamp=" + importTimestamp +
                '}';
    }
}