package com.arjunakankipati.racingstatanalysis.dto;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Data Transfer Object for Session information.
 */
public class SessionDTO {
    private Long id;
    private Long eventId;
    private String name;
    private String type;
    private LocalDateTime startDatetime;
    private Integer durationSeconds;

    /**
     * Default constructor.
     */
    public SessionDTO() {
    }

    /**
     * Full constructor.
     *
     * @param id               the ID of the session
     * @param eventId          the ID of the event this session belongs to
     * @param name             the name of the session
     * @param type             the type of the session (e.g., "Race", "Practice", "Qualifying")
     * @param startDatetime    the start date and time of the session
     * @param durationSeconds  the duration of the session in seconds
     */
    public SessionDTO(Long id, Long eventId, String name, String type, LocalDateTime startDatetime,
                      Integer durationSeconds) {
        this.id = id;
        this.eventId = eventId;
        this.name = name;
        this.type = type;
        this.startDatetime = startDatetime;
        this.durationSeconds = durationSeconds;
    }

    /**
     * Gets the ID of the session.
     *
     * @return the ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the ID of the session.
     *
     * @param id the ID to set
     */
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SessionDTO that = (SessionDTO) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(eventId, that.eventId) &&
                Objects.equals(name, that.name) &&
                Objects.equals(type, that.type) &&
                Objects.equals(startDatetime, that.startDatetime) &&
                Objects.equals(durationSeconds, that.durationSeconds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, eventId, name, type, startDatetime, durationSeconds);
    }

    @Override
    public String toString() {
        return "SessionDTO{" +
                "id=" + id +
                ", eventId=" + eventId +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", startDatetime=" + startDatetime +
                ", durationSeconds=" + durationSeconds +
                '}';
    }
} 