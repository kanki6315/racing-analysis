package com.arjunakankipati.racingstatanalysis.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Data Transfer Object for driver lap times.
 * Contains lap times for a specific driver in a session.
 */
public class DriverLapTimesDTO {
    private Long driverId;
    private String driverName;
    private String carNumber;
    private String teamName;
    private String carModel;
    private String className;
    private List<LapTimeDetailDTO> lapTimes;

    /**
     * Default constructor.
     */
    public DriverLapTimesDTO() {
    }

    /**
     * Full constructor.
     *
     * @param driverId   the ID of the driver
     * @param driverName the name of the driver
     * @param carNumber  the car number
     * @param teamName   the team name
     * @param carModel   the car model
     * @param className  the class name
     * @param lapTimes   the list of lap times
     */
    public DriverLapTimesDTO(Long driverId, String driverName, String carNumber, String teamName,
                             String carModel, String className, List<LapTimeDetailDTO> lapTimes) {
        this.driverId = driverId;
        this.driverName = driverName;
        this.carNumber = carNumber;
        this.teamName = teamName;
        this.carModel = carModel;
        this.className = className;
        this.lapTimes = lapTimes;
    }

    /**
     * Gets the driver ID.
     *
     * @return the driver ID
     */
    public Long getDriverId() {
        return driverId;
    }

    /**
     * Sets the driver ID.
     *
     * @param driverId the driver ID to set
     */
    public void setDriverId(Long driverId) {
        this.driverId = driverId;
    }

    /**
     * Gets the driver name.
     *
     * @return the driver name
     */
    public String getDriverName() {
        return driverName;
    }

    /**
     * Sets the driver name.
     *
     * @param driverName the driver name to set
     */
    public void setDriverName(String driverName) {
        this.driverName = driverName;
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
    public String getTeamName() {
        return teamName;
    }

    /**
     * Sets the team name.
     *
     * @param teamName the team name to set
     */
    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    /**
     * Gets the car model.
     *
     * @return the car model
     */
    public String getCarModel() {
        return carModel;
    }

    /**
     * Sets the car model.
     *
     * @param carModel the car model to set
     */
    public void setCarModel(String carModel) {
        this.carModel = carModel;
    }

    /**
     * Gets the class name.
     *
     * @return the class name
     */
    public String getClassName() {
        return className;
    }

    /**
     * Sets the class name.
     *
     * @param className the class name to set
     */
    public void setClassName(String className) {
        this.className = className;
    }

    /**
     * Gets the list of lap times.
     *
     * @return the lap times
     */
    public List<LapTimeDetailDTO> getLapTimes() {
        return lapTimes;
    }

    /**
     * Sets the list of lap times.
     *
     * @param lapTimes the lap times to set
     */
    public void setLapTimes(List<LapTimeDetailDTO> lapTimes) {
        this.lapTimes = lapTimes;
    }
} 