package com.arjunakankipati.racingstatanalysis.model;

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
     */
    public Lap(Long id, Long carId, Long driverId, Integer lapNumber, BigDecimal lapTimeSeconds,
               BigDecimal sessionElapsedSeconds, LocalDateTime timestamp, BigDecimal averageSpeedKph) {
        this.id = id;
        this.carId = carId;
        this.driverId = driverId;
        this.lapNumber = lapNumber;
        this.lapTimeSeconds = lapTimeSeconds;
        this.sessionElapsedSeconds = sessionElapsedSeconds;
        this.timestamp = timestamp;
        this.averageSpeedKph = averageSpeedKph;
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
                Objects.equals(averageSpeedKph, lap.averageSpeedKph);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, carId, driverId, lapNumber, lapTimeSeconds, sessionElapsedSeconds,
                timestamp, averageSpeedKph);
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
                ", averageSpeedKph=" + averageSpeedKph + '\'' +
                '}';
    }
}