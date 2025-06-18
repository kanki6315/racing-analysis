package com.arjunakankipati.racingstatanalysis.dto;

import java.math.BigDecimal;

/**
 * Data Transfer Object for lap time analysis data.
 * Contains statistical information about lap times for an event.
 */
public class LapTimeAnalysisDTO {
    private String averageLapTime;
    private String fastestLapTime;
    private String medianLapTime;
    private Integer totalLapCount;
    private Long eventId;

    /**
     * Default constructor.
     */
    public LapTimeAnalysisDTO() {
    }

    /**
     * Full constructor.
     *
     * @param averageLapTime the average lap time in format "m:ss.SSS"
     * @param fastestLapTime the fastest lap time in format "m:ss.SSS"
     * @param medianLapTime  the median lap time in format "m:ss.SSS"
     * @param totalLapCount  the total number of laps
     * @param eventId        the ID of the event
     */
    public LapTimeAnalysisDTO(String averageLapTime, String fastestLapTime, String medianLapTime,
                              Integer totalLapCount, Long eventId) {
        this.averageLapTime = averageLapTime;
        this.fastestLapTime = fastestLapTime;
        this.medianLapTime = medianLapTime;
        this.totalLapCount = totalLapCount;
        this.eventId = eventId;
    }

    /**
     * Gets the average lap time.
     *
     * @return the average lap time
     */
    public String getAverageLapTime() {
        return averageLapTime;
    }

    /**
     * Sets the average lap time.
     *
     * @param averageLapTime the average lap time to set
     */
    public void setAverageLapTime(String averageLapTime) {
        this.averageLapTime = averageLapTime;
    }

    /**
     * Gets the fastest lap time.
     *
     * @return the fastest lap time
     */
    public String getFastestLapTime() {
        return fastestLapTime;
    }

    /**
     * Sets the fastest lap time.
     *
     * @param fastestLapTime the fastest lap time to set
     */
    public void setFastestLapTime(String fastestLapTime) {
        this.fastestLapTime = fastestLapTime;
    }

    /**
     * Gets the median lap time.
     *
     * @return the median lap time
     */
    public String getMedianLapTime() {
        return medianLapTime;
    }

    /**
     * Sets the median lap time.
     *
     * @param medianLapTime the median lap time to set
     */
    public void setMedianLapTime(String medianLapTime) {
        this.medianLapTime = medianLapTime;
    }

    /**
     * Gets the total number of laps.
     *
     * @return the total lap count
     */
    public Integer getTotalLapCount() {
        return totalLapCount;
    }

    /**
     * Sets the total number of laps.
     *
     * @param totalLapCount the total lap count to set
     */
    public void setTotalLapCount(Integer totalLapCount) {
        this.totalLapCount = totalLapCount;
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
}