package com.arjunakankipati.racingstatanalysis.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Response DTO for sessions in an event.
 */
public class SessionsResponseDTO {
    private Long eventId;
    private String eventName;
    private List<SessionDTO> sessions;

    /**
     * Default constructor.
     */
    public SessionsResponseDTO() {
        this.sessions = new ArrayList<>();
    }

    /**
     * Constructor with event information.
     *
     * @param eventId   the ID of the event
     * @param eventName the name of the event
     */
    public SessionsResponseDTO(Long eventId, String eventName) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.sessions = new ArrayList<>();
    }

    /**
     * Constructor with event information and sessions.
     *
     * @param eventId   the ID of the event
     * @param eventName the name of the event
     * @param sessions  the list of sessions
     */
    public SessionsResponseDTO(Long eventId, String eventName, List<SessionDTO> sessions) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.sessions = sessions != null ? sessions : new ArrayList<>();
    }

    /**
     * Gets the ID of the event.
     *
     * @return the event ID
     */
    public Long getEventId() {
        return eventId;
    }

    /**
     * Sets the ID of the event.
     *
     * @param eventId the event ID to set
     */
    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    /**
     * Gets the name of the event.
     *
     * @return the event name
     */
    public String getEventName() {
        return eventName;
    }

    /**
     * Sets the name of the event.
     *
     * @param eventName the event name to set
     */
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    /**
     * Gets the list of sessions.
     *
     * @return the sessions
     */
    public List<SessionDTO> getSessions() {
        return sessions;
    }

    /**
     * Sets the list of sessions.
     *
     * @param sessions the sessions to set
     */
    public void setSessions(List<SessionDTO> sessions) {
        this.sessions = sessions != null ? sessions : new ArrayList<>();
    }

    /**
     * Adds a session to the list.
     *
     * @param sessionDTO the session to add
     */
    public void addSession(SessionDTO sessionDTO) {
        if (this.sessions == null) {
            this.sessions = new ArrayList<>();
        }
        this.sessions.add(sessionDTO);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SessionsResponseDTO that = (SessionsResponseDTO) o;
        return Objects.equals(eventId, that.eventId) &&
                Objects.equals(eventName, that.eventName) &&
                Objects.equals(sessions, that.sessions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId, eventName, sessions);
    }

    @Override
    public String toString() {
        return "SessionsResponseDTO{" +
                "eventId=" + eventId +
                ", eventName='" + eventName + '\'' +
                ", sessions=" + sessions +
                '}';
    }
} 