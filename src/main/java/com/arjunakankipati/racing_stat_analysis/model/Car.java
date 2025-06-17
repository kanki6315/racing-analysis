package com.arjunakankipati.racing_stat_analysis.model;

import java.util.Objects;

/**
 * Represents a racing car (e.g., "BMW M4 GT3 EVO").
 * A car participates in racing sessions as part of a team and belongs to a specific class.
 */
public class Car implements BaseEntity<Long> {
    private Long id;
    private Long sessionId;
    private Long teamId;
    private Long classId;
    private Long manufacturerId;
    private String number;
    private String model;
    private String tireSupplier;

    /**
     * Default constructor.
     */
    public Car() {
    }

    /**
     * Full constructor.
     *
     * @param id the ID of the car
     * @param sessionId the ID of the session this car participates in
     * @param teamId the ID of the team this car belongs to
     * @param classId the ID of the class this car belongs to
     * @param manufacturerId the ID of the manufacturer of this car
     * @param number the number of the car
     * @param model the model of the car
     * @param tireSupplier the tire supplier of the car
     */
    public Car(Long id, Long sessionId, Long teamId, Long classId, Long manufacturerId, String number, String model, String tireSupplier) {
        this.id = id;
        this.sessionId = sessionId;
        this.teamId = teamId;
        this.classId = classId;
        this.manufacturerId = manufacturerId;
        this.number = number;
        this.model = model;
        this.tireSupplier = tireSupplier;
    }

    /**
     * Gets the ID of the car.
     *
     * @return the ID
     */
    @Override
    public Long getId() {
        return id;
    }

    /**
     * Sets the ID of the car.
     *
     * @param id the ID to set
     */
    @Override
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the ID of the session this car participates in.
     *
     * @return the session ID
     */
    public Long getSessionId() {
        return sessionId;
    }

    /**
     * Sets the ID of the session this car participates in.
     *
     * @param sessionId the session ID to set
     */
    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    /**
     * Gets the ID of the team this car belongs to.
     *
     * @return the team ID
     */
    public Long getTeamId() {
        return teamId;
    }

    /**
     * Sets the ID of the team this car belongs to.
     *
     * @param teamId the team ID to set
     */
    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }

    /**
     * Gets the ID of the class this car belongs to.
     *
     * @return the class ID
     */
    public Long getClassId() {
        return classId;
    }

    /**
     * Sets the ID of the class this car belongs to.
     *
     * @param classId the class ID to set
     */
    public void setClassId(Long classId) {
        this.classId = classId;
    }

    /**
     * Gets the ID of the manufacturer of this car.
     *
     * @return the manufacturer ID
     */
    public Long getManufacturerId() {
        return manufacturerId;
    }

    /**
     * Sets the ID of the manufacturer of this car.
     *
     * @param manufacturerId the manufacturer ID to set
     */
    public void setManufacturerId(Long manufacturerId) {
        this.manufacturerId = manufacturerId;
    }

    /**
     * Gets the number of the car.
     *
     * @return the number
     */
    public String getNumber() {
        return number;
    }

    /**
     * Sets the number of the car.
     *
     * @param number the number to set
     */
    public void setNumber(String number) {
        this.number = number;
    }

    /**
     * Gets the model of the car.
     *
     * @return the model
     */
    public String getModel() {
        return model;
    }

    /**
     * Sets the model of the car.
     *
     * @param model the model to set
     */
    public void setModel(String model) {
        this.model = model;
    }

    /**
     * Gets the tire supplier of the car.
     *
     * @return the tire supplier
     */
    public String getTireSupplier() {
        return tireSupplier;
    }

    /**
     * Sets the tire supplier of the car.
     *
     * @param tireSupplier the tire supplier to set
     */
    public void setTireSupplier(String tireSupplier) {
        this.tireSupplier = tireSupplier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Car car = (Car) o;
        return Objects.equals(id, car.id) &&
                Objects.equals(sessionId, car.sessionId) &&
                Objects.equals(teamId, car.teamId) &&
                Objects.equals(classId, car.classId) &&
                Objects.equals(manufacturerId, car.manufacturerId) &&
                Objects.equals(number, car.number) &&
                Objects.equals(model, car.model) &&
                Objects.equals(tireSupplier, car.tireSupplier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, sessionId, teamId, classId, manufacturerId, number, model, tireSupplier);
    }

    @Override
    public String toString() {
        return "Car{" +
                "id=" + id +
                ", sessionId=" + sessionId +
                ", teamId=" + teamId +
                ", classId=" + classId +
                ", manufacturerId=" + manufacturerId +
                ", number='" + number + '\'' +
                ", model='" + model + '\'' +
                ", tireSupplier='" + tireSupplier + '\'' +
                '}';
    }
}