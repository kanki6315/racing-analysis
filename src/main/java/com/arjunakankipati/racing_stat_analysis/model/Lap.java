package com.arjunakankipati.racing_stat_analysis.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a lap completed by a driver in a car during a session.
 * Contains timing and performance data for the lap.
 */
public class Lap implements BaseEntity<Long> {
    private Long id;
    private Long carId;
    private Long driverId;
    private Integer lapNumber;
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
    public Lap() {
    }

    /**
     * Full constructor.
     *
     * @param id the ID of the lap
     * @param carId the ID of the car that completed the lap
     * @param driverId the ID of the driver who drove the lap
     * @param lapNumber the number of the lap
     * @param lapTimeSeconds the time taken to complete the lap in seconds
     * @param sessionElapsedSeconds the elapsed time of the session when the lap was completed in seconds
     * @param timestamp the timestamp when the lap was completed
     * @param averageSpeedKph the average speed of the lap in kilometers per hour
     * @param isValid whether the lap is valid
     * @param isPersonalBest whether the lap is the personal best for the driver
     * @param isSessionBest whether the lap is the best in the session
     * @param invalidationReason the reason for invalidation if the lap is invalid
     */
    public Lap(Long id, Long carId, Long driverId, Integer lapNumber, BigDecimal lapTimeSeconds,
              BigDecimal sessionElapsedSeconds, LocalDateTime timestamp, BigDecimal averageSpeedKph,
              Boolean isValid, Boolean isPersonalBest, Boolean isSessionBest, String invalidationReason) {
        this.id = id;
        this.carId = carId;
        this.driverId = driverId;
        this.lapNumber = lapNumber;
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
     * Gets the ID of the lap.
     *
     * @return the ID
     */
    @Override
    public Long getId() {
        return id;
    }

    /**
     * Sets the ID of the lap.
     *
     * @param id the ID to set
     */
    @Override
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the ID of the car that completed the lap.
     *
     * @return the car ID
     */
    public Long getCarId() {
        return carId;
    }

    /**
     * Sets the ID of the car that completed the lap.
     *
     * @param carId the car ID to set
     */
    public void setCarId(Long carId) {
        this.carId = carId;
    }

    /**
     * Gets the ID of the driver who drove the lap.
     *
     * @return the driver ID
     */
    public Long getDriverId() {
        return driverId;
    }

    /**
     * Sets the ID of the driver who drove the lap.
     *
     * @param driverId the driver ID to set
     */
    public void setDriverId(Long driverId) {
        this.driverId = driverId;
    }

    /**
     * Gets the number of the lap.
     *
     * @return the lap number
     */
    public Integer getLapNumber() {
        return lapNumber;
    }

    /**
     * Sets the number of the lap.
     *
     * @param lapNumber the lap number to set
     */
    public void setLapNumber(Integer lapNumber) {
        this.lapNumber = lapNumber;
    }

    /**
     * Gets the time taken to complete the lap in seconds.
     *
     * @return the lap time in seconds
     */
    public BigDecimal getLapTimeSeconds() {
        return lapTimeSeconds;
    }

    /**
     * Sets the time taken to complete the lap in seconds.
     *
     * @param lapTimeSeconds the lap time in seconds to set
     */
    public void setLapTimeSeconds(BigDecimal lapTimeSeconds) {
        this.lapTimeSeconds = lapTimeSeconds;
    }

    /**
     * Gets the elapsed time of the session when the lap was completed in seconds.
     *
     * @return the session elapsed time in seconds
     */
    public BigDecimal getSessionElapsedSeconds() {
        return sessionElapsedSeconds;
    }

    /**
     * Sets the elapsed time of the session when the lap was completed in seconds.
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
     * Gets the average speed of the lap in kilometers per hour.
     *
     * @return the average speed in kilometers per hour
     */
    public BigDecimal getAverageSpeedKph() {
        return averageSpeedKph;
    }

    /**
     * Sets the average speed of the lap in kilometers per hour.
     *
     * @param averageSpeedKph the average speed in kilometers per hour to set
     */
    public void setAverageSpeedKph(BigDecimal averageSpeedKph) {
        this.averageSpeedKph = averageSpeedKph;
    }

    /**
     * Gets whether the lap is valid.
     *
     * @return true if the lap is valid, false otherwise
     */
    public Boolean getIsValid() {
        return isValid;
    }

    /**
     * Sets whether the lap is valid.
     *
     * @param isValid true if the lap is valid, false otherwise
     */
    public void setIsValid(Boolean isValid) {
        this.isValid = isValid;
    }

    /**
     * Gets whether the lap is the personal best for the driver.
     *
     * @return true if the lap is the personal best, false otherwise
     */
    public Boolean getIsPersonalBest() {
        return isPersonalBest;
    }

    /**
     * Sets whether the lap is the personal best for the driver.
     *
     * @param isPersonalBest true if the lap is the personal best, false otherwise
     */
    public void setIsPersonalBest(Boolean isPersonalBest) {
        this.isPersonalBest = isPersonalBest;
    }

    /**
     * Gets whether the lap is the best in the session.
     *
     * @return true if the lap is the session best, false otherwise
     */
    public Boolean getIsSessionBest() {
        return isSessionBest;
    }

    /**
     * Sets whether the lap is the best in the session.
     *
     * @param isSessionBest true if the lap is the session best, false otherwise
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Lap lap = (Lap) o;
        return Objects.equals(id, lap.id) &&
                Objects.equals(carId, lap.carId) &&
                Objects.equals(driverId, lap.driverId) &&
                Objects.equals(lapNumber, lap.lapNumber) &&
                Objects.equals(lapTimeSeconds, lap.lapTimeSeconds) &&
                Objects.equals(sessionElapsedSeconds, lap.sessionElapsedSeconds) &&
                Objects.equals(timestamp, lap.timestamp) &&
                Objects.equals(averageSpeedKph, lap.averageSpeedKph) &&
                Objects.equals(isValid, lap.isValid) &&
                Objects.equals(isPersonalBest, lap.isPersonalBest) &&
                Objects.equals(isSessionBest, lap.isSessionBest) &&
                Objects.equals(invalidationReason, lap.invalidationReason);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, carId, driverId, lapNumber, lapTimeSeconds, sessionElapsedSeconds,
                timestamp, averageSpeedKph, isValid, isPersonalBest, isSessionBest, invalidationReason);
    }

    @Override
    public String toString() {
        return "Lap{" +
                "id=" + id +
                ", carId=" + carId +
                ", driverId=" + driverId +
                ", lapNumber=" + lapNumber +
                ", lapTimeSeconds=" + lapTimeSeconds +
                ", sessionElapsedSeconds=" + sessionElapsedSeconds +
                ", timestamp=" + timestamp +
                ", averageSpeedKph=" + averageSpeedKph +
                ", isValid=" + isValid +
                ", isPersonalBest=" + isPersonalBest +
                ", isSessionBest=" + isSessionBest +
                ", invalidationReason='" + invalidationReason + '\'' +
                '}';
    }
}