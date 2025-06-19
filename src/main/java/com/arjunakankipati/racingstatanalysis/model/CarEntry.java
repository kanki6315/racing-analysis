package com.arjunakankipati.racingstatanalysis.model;

import java.util.Objects;

/**
 * Represents a car entry in a racing session (e.g., "Car #01 from Team BMW").
 * A car entry is an individual car participating in a specific session.
 */
public class CarEntry implements BaseEntity<Long> {
    private Long id;
    private Long sessionId;
    private Long teamId;
    private Long classId;
    private Long carModelId;
    private String number;
    private String tireSupplier;

    /**
     * Default constructor.
     */
    public CarEntry() {
    }

    /**
     * Full constructor.
     *
     * @param id           the ID of the car entry
     * @param sessionId    the ID of the session this car entry participates in
     * @param teamId       the ID of the team this car entry belongs to
     * @param classId      the ID of the class this car entry belongs to
     * @param carModelId   the ID of the car model this car entry represents
     * @param number       the number of the car entry
     * @param tireSupplier the tire supplier of the car entry
     */
    public CarEntry(Long id, Long sessionId, Long teamId, Long classId, Long carModelId, String number, String tireSupplier) {
        this.id = id;
        this.sessionId = sessionId;
        this.teamId = teamId;
        this.classId = classId;
        this.carModelId = carModelId;
        this.number = number;
        this.tireSupplier = tireSupplier;
    }

    /**
     * Gets the ID of the car entry.
     *
     * @return the ID
     */
    @Override
    public Long getId() {
        return id;
    }

    /**
     * Sets the ID of the car entry.
     *
     * @param id the ID to set
     */
    @Override
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the ID of the session this car entry participates in.
     *
     * @return the session ID
     */
    public Long getSessionId() {
        return sessionId;
    }

    /**
     * Sets the ID of the session this car entry participates in.
     *
     * @param sessionId the session ID to set
     */
    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    /**
     * Gets the ID of the team this car entry belongs to.
     *
     * @return the team ID
     */
    public Long getTeamId() {
        return teamId;
    }

    /**
     * Sets the ID of the team this car entry belongs to.
     *
     * @param teamId the team ID to set
     */
    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }

    /**
     * Gets the ID of the class this car entry belongs to.
     *
     * @return the class ID
     */
    public Long getClassId() {
        return classId;
    }

    /**
     * Sets the ID of the class this car entry belongs to.
     *
     * @param classId the class ID to set
     */
    public void setClassId(Long classId) {
        this.classId = classId;
    }

    /**
     * Gets the ID of the car model this car entry represents.
     *
     * @return the car model ID
     */
    public Long getCarModelId() {
        return carModelId;
    }

    /**
     * Sets the ID of the car model this car entry represents.
     *
     * @param carModelId the car model ID to set
     */
    public void setCarModelId(Long carModelId) {
        this.carModelId = carModelId;
    }

    /**
     * Gets the number of the car entry.
     *
     * @return the number
     */
    public String getNumber() {
        return number;
    }

    /**
     * Sets the number of the car entry.
     *
     * @param number the number to set
     */
    public void setNumber(String number) {
        this.number = number;
    }

    /**
     * Gets the tire supplier of the car entry.
     *
     * @return the tire supplier
     */
    public String getTireSupplier() {
        return tireSupplier;
    }

    /**
     * Sets the tire supplier of the car entry.
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
        CarEntry carEntry = (CarEntry) o;
        return Objects.equals(id, carEntry.id) &&
                Objects.equals(sessionId, carEntry.sessionId) &&
                Objects.equals(teamId, carEntry.teamId) &&
                Objects.equals(classId, carEntry.classId) &&
                Objects.equals(carModelId, carEntry.carModelId) &&
                Objects.equals(number, carEntry.number) &&
                Objects.equals(tireSupplier, carEntry.tireSupplier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, sessionId, teamId, classId, carModelId, number, tireSupplier);
    }

    @Override
    public String toString() {
        return "CarEntry{" +
                "id=" + id +
                ", sessionId=" + sessionId +
                ", teamId=" + teamId +
                ", classId=" + classId +
                ", carModelId=" + carModelId +
                ", number='" + number + '\'' +
                ", tireSupplier='" + tireSupplier + '\'' +
                '}';
    }
} 