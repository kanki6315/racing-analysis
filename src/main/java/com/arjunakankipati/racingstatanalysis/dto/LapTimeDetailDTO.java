package com.arjunakankipati.racingstatanalysis.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for detailed lap time information.
 * Contains comprehensive information about a specific lap.
 */
public class LapTimeDetailDTO {
    private Long lapId;
    private Integer lapNumber;
    private String lapTime;
    private BigDecimal lapTimeSeconds;
    private BigDecimal sessionElapsedSeconds;
    private LocalDateTime timestamp;
    private BigDecimal averageSpeedKph;
    private Boolean isValid;
    private Boolean isPersonalBest;
    private Boolean isSessionBest;
    private String invalidationReason;

    /**
     * Default constructor.
     */
    public LapTimeDetailDTO() {
    }

    /**
     * Full constructor.
     *
     * @param lapId                 the ID of the lap
     * @param lapNumber             the lap number
     * @param lapTime               the formatted lap time
     * @param lapTimeSeconds        the lap time in seconds
     * @param sessionElapsedSeconds the session elapsed time in seconds
     * @param timestamp             the timestamp when the lap was completed
     * @param averageSpeedKph       the average speed in kilometers per hour
     * @param isValid               whether the lap is valid
     * @param isPersonalBest        whether the lap is the personal best for the driver
     * @param isSessionBest         whether the lap is the best in the session
     * @param invalidationReason    the reason for invalidation if the lap is invalid
     */
    public LapTimeDetailDTO(Long lapId, Integer lapNumber, String lapTime, BigDecimal lapTimeSeconds,
                            BigDecimal sessionElapsedSeconds, LocalDateTime timestamp, BigDecimal averageSpeedKph,
                            Boolean isValid, Boolean isPersonalBest, Boolean isSessionBest, String invalidationReason) {
        this.lapId = lapId;
        this.lapNumber = lapNumber;
        this.lapTime = lapTime;
        this.lapTimeSeconds = lapTimeSeconds;
        this.sessionElapsedSeconds = sessionElapsedSeconds;
        this.timestamp = timestamp;
        this.averageSpeedKph = averageSpeedKph;
        this.isValid = isValid;
        this.isPersonalBest = isPersonalBest;
        this.isSessionBest = isSessionBest;
        this.invalidationReason = invalidationReason;
    }

    /**
     * Gets the lap ID.
     *
     * @return the lap ID
     */
    public Long getLapId() {
        return lapId;
    }

    /**
     * Sets the lap ID.
     *
     * @param lapId the lap ID to set
     */
    public void setLapId(Long lapId) {
        this.lapId = lapId;
    }

    /**
     * Gets the lap number.
     *
     * @return the lap number
     */
    public Integer getLapNumber() {
        return lapNumber;
    }

    /**
     * Sets the lap number.
     *
     * @param lapNumber the lap number to set
     */
    public void setLapNumber(Integer lapNumber) {
        this.lapNumber = lapNumber;
    }

    /**
     * Gets the formatted lap time.
     *
     * @return the formatted lap time
     */
    public String getLapTime() {
        return lapTime;
    }

    /**
     * Sets the formatted lap time.
     *
     * @param lapTime the formatted lap time to set
     */
    public void setLapTime(String lapTime) {
        this.lapTime = lapTime;
    }

    /**
     * Gets the lap time in seconds.
     *
     * @return the lap time in seconds
     */
    public BigDecimal getLapTimeSeconds() {
        return lapTimeSeconds;
    }

    /**
     * Sets the lap time in seconds.
     *
     * @param lapTimeSeconds the lap time in seconds to set
     */
    public void setLapTimeSeconds(BigDecimal lapTimeSeconds) {
        this.lapTimeSeconds = lapTimeSeconds;
    }

    /**
     * Gets the session elapsed time in seconds.
     *
     * @return the session elapsed time in seconds
     */
    public BigDecimal getSessionElapsedSeconds() {
        return sessionElapsedSeconds;
    }

    /**
     * Sets the session elapsed time in seconds.
     *
     * @param sessionElapsedSeconds the session elapsed time in seconds to set
     */
    public void setSessionElapsedSeconds(BigDecimal sessionElapsedSeconds) {
        this.sessionElapsedSeconds = sessionElapsedSeconds;
    }

    /**
     * Gets the timestamp when the lap was completed.
     *
     * @return the timestamp
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the timestamp when the lap was completed.
     *
     * @param timestamp the timestamp to set
     */
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Gets the average speed in kilometers per hour.
     *
     * @return the average speed in kilometers per hour
     */
    public BigDecimal getAverageSpeedKph() {
        return averageSpeedKph;
    }

    /**
     * Sets the average speed in kilometers per hour.
     *
     * @param averageSpeedKph the average speed in kilometers per hour to set
     */
    public void setAverageSpeedKph(BigDecimal averageSpeedKph) {
        this.averageSpeedKph = averageSpeedKph;
    }

    /**
     * Gets whether the lap is valid.
     *
     * @return whether the lap is valid
     */
    public Boolean getIsValid() {
        return isValid;
    }

    /**
     * Sets whether the lap is valid.
     *
     * @param isValid whether the lap is valid to set
     */
    public void setIsValid(Boolean isValid) {
        this.isValid = isValid;
    }

    /**
     * Gets whether the lap is the personal best for the driver.
     *
     * @return whether the lap is the personal best
     */
    public Boolean getIsPersonalBest() {
        return isPersonalBest;
    }

    /**
     * Sets whether the lap is the personal best for the driver.
     *
     * @param isPersonalBest whether the lap is the personal best to set
     */
    public void setIsPersonalBest(Boolean isPersonalBest) {
        this.isPersonalBest = isPersonalBest;
    }

    /**
     * Gets whether the lap is the best in the session.
     *
     * @return whether the lap is the session best
     */
    public Boolean getIsSessionBest() {
        return isSessionBest;
    }

    /**
     * Sets whether the lap is the best in the session.
     *
     * @param isSessionBest whether the lap is the session best to set
     */
    public void setIsSessionBest(Boolean isSessionBest) {
        this.isSessionBest = isSessionBest;
    }

    /**
     * Gets the reason for invalidation if the lap is invalid.
     *
     * @return the invalidation reason
     */
    public String getInvalidationReason() {
        return invalidationReason;
    }

    /**
     * Sets the reason for invalidation if the lap is invalid.
     *
     * @param invalidationReason the invalidation reason to set
     */
    public void setInvalidationReason(String invalidationReason) {
        this.invalidationReason = invalidationReason;
    }
} 