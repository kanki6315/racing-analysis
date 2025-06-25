package com.arjunakankipati.racingstatanalysis.dto;

import java.util.List;

/**
 * Data Transfer Object for lap times response.
 * Contains lap times for multiple drivers in a session.
 */
public class LapTimesResponseDTO {
    private Long eventId;
    private Long sessionId;
    private List<DriverLapTimesDTO> driverLapTimes;

    /**
     * Default constructor.
     */
    public LapTimesResponseDTO() {
    }

    /**
     * Full constructor.
     *
     * @param eventId        the ID of the event
     * @param sessionId      the ID of the session
     * @param driverLapTimes the list of driver lap times
     */
    public LapTimesResponseDTO(Long eventId, Long sessionId, List<DriverLapTimesDTO> driverLapTimes) {
        this.eventId = eventId;
        this.sessionId = sessionId;
        this.driverLapTimes = driverLapTimes;
    }

    /**
     * Gets the event ID.
     *
     * @return the event ID
     */
    public Long getEventId() {
        return eventId;
    }

    /**
     * Sets the event ID.
     *
     * @param eventId the event ID to set
     */
    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    /**
     * Gets the session ID.
     *
     * @return the session ID
     */
    public Long getSessionId() {
        return sessionId;
    }

    /**
     * Sets the session ID.
     *
     * @param sessionId the session ID to set
     */
    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    /**
     * Gets the list of driver lap times.
     *
     * @return the driver lap times
     */
    public List<DriverLapTimesDTO> getDriverLapTimes() {
        return driverLapTimes;
    }

    /**
     * Sets the list of driver lap times.
     *
     * @param driverLapTimes the driver lap times to set
     */
    public void setDriverLapTimes(List<DriverLapTimesDTO> driverLapTimes) {
        this.driverLapTimes = driverLapTimes;
    }
} 