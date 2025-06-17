package com.arjunakankipati.racingstatanalysis.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for lap time data.
 * Contains information about a lap time.
 */
public class LapTimeDTO {
    private String lapTime;
    private BigDecimal averageSpeedKph;
    private String event;
    private Integer year;
    private String carNumber;
    private String team;
    private String driver;
    private LocalDateTime timestamp;

    /**
     * Default constructor.
     */
    public LapTimeDTO() {
    }

    /**
     * Full constructor.
     *
     * @param lapTime the lap time in format "m:ss.SSS"
     * @param averageSpeedKph the average speed in kilometers per hour
     * @param event the name of the event
     * @param year the year of the event
     * @param carNumber the car number
     * @param team the team name
     * @param driver the driver name
     * @param timestamp the timestamp when the lap was completed
     */
    public LapTimeDTO(String lapTime, BigDecimal averageSpeedKph, String event, Integer year,
                     String carNumber, String team, String driver, LocalDateTime timestamp) {
        this.lapTime = lapTime;
        this.averageSpeedKph = averageSpeedKph;
        this.event = event;
        this.year = year;
        this.carNumber = carNumber;
        this.team = team;
        this.driver = driver;
        this.timestamp = timestamp;
    }

    /**
     * Gets the lap time.
     *
     * @return the lap time
     */
    public String getLapTime() {
        return lapTime;
    }

    /**
     * Sets the lap time.
     *
     * @param lapTime the lap time to set
     */
    public void setLapTime(String lapTime) {
        this.lapTime = lapTime;
    }

    /**
     * Gets the average speed in kilometers per hour.
     *
     * @return the average speed
     */
    public BigDecimal getAverageSpeedKph() {
        return averageSpeedKph;
    }

    /**
     * Sets the average speed in kilometers per hour.
     *
     * @param averageSpeedKph the average speed to set
     */
    public void setAverageSpeedKph(BigDecimal averageSpeedKph) {
        this.averageSpeedKph = averageSpeedKph;
    }

    /**
     * Gets the name of the event.
     *
     * @return the event name
     */
    public String getEvent() {
        return event;
    }

    /**
     * Sets the name of the event.
     *
     * @param event the event name to set
     */
    public void setEvent(String event) {
        this.event = event;
    }

    /**
     * Gets the year of the event.
     *
     * @return the year
     */
    public Integer getYear() {
        return year;
    }

    /**
     * Sets the year of the event.
     *
     * @param year the year to set
     */
    public void setYear(Integer year) {
        this.year = year;
    }

    /**
     * Gets the car number.
     *
     * @return the car number
     */
    public String getCarNumber() {
        return carNumber;
    }

    /**
     * Sets the car number.
     *
     * @param carNumber the car number to set
     */
    public void setCarNumber(String carNumber) {
        this.carNumber = carNumber;
    }

    /**
     * Gets the team name.
     *
     * @return the team name
     */
    public String getTeam() {
        return team;
    }

    /**
     * Sets the team name.
     *
     * @param team the team name to set
     */
    public void setTeam(String team) {
        this.team = team;
    }

    /**
     * Gets the driver name.
     *
     * @return the driver name
     */
    public String getDriver() {
        return driver;
    }

    /**
     * Sets the driver name.
     *
     * @param driver the driver name to set
     */
    public void setDriver(String driver) {
        this.driver = driver;
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
}