package com.arjunakankipati.racingstatanalysis.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Response DTO for cars in a specific class for an event.
 */
public class CarsResponseDTO {
    private Long eventId;
    private String eventName;
    private Long classId;
    private String className;
    private List<CarDTO> cars;

    /**
     * Default constructor.
     */
    public CarsResponseDTO() {
        this.cars = new ArrayList<>();
    }

    /**
     * Constructor with event and class information.
     *
     * @param eventId   the ID of the event
     * @param eventName the name of the event
     * @param classId   the ID of the class
     * @param className the name of the class
     */
    public CarsResponseDTO(Long eventId, String eventName, Long classId, String className) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.classId = classId;
        this.className = className;
        this.cars = new ArrayList<>();
    }

    /**
     * Constructor with event, class information and cars.
     *
     * @param eventId   the ID of the event
     * @param eventName the name of the event
     * @param classId   the ID of the class
     * @param className the name of the class
     * @param cars      the list of cars
     */
    public CarsResponseDTO(Long eventId, String eventName, Long classId, String className, List<CarDTO> cars) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.classId = classId;
        this.className = className;
        this.cars = cars != null ? cars : new ArrayList<>();
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
     * Gets the ID of the class.
     *
     * @return the class ID
     */
    public Long getClassId() {
        return classId;
    }

    /**
     * Sets the ID of the class.
     *
     * @param classId the class ID to set
     */
    public void setClassId(Long classId) {
        this.classId = classId;
    }

    /**
     * Gets the name of the class.
     *
     * @return the class name
     */
    public String getClassName() {
        return className;
    }

    /**
     * Sets the name of the class.
     *
     * @param className the class name to set
     */
    public void setClassName(String className) {
        this.className = className;
    }

    /**
     * Gets the list of cars.
     *
     * @return the cars
     */
    public List<CarDTO> getCars() {
        return cars;
    }

    /**
     * Sets the list of cars.
     *
     * @param cars the cars to set
     */
    public void setCars(List<CarDTO> cars) {
        this.cars = cars != null ? cars : new ArrayList<>();
    }

    /**
     * Adds a car to the list.
     *
     * @param carDTO the car to add
     */
    public void addCar(CarDTO carDTO) {
        if (this.cars == null) {
            this.cars = new ArrayList<>();
        }
        this.cars.add(carDTO);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CarsResponseDTO that = (CarsResponseDTO) o;
        return Objects.equals(eventId, that.eventId) &&
                Objects.equals(eventName, that.eventName) &&
                Objects.equals(classId, that.classId) &&
                Objects.equals(className, that.className) &&
                Objects.equals(cars, that.cars);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId, eventName, classId, className, cars);
    }

    @Override
    public String toString() {
        return "CarsResponseDTO{" +
                "eventId=" + eventId +
                ", eventName='" + eventName + '\'' +
                ", classId=" + classId +
                ", className='" + className + '\'' +
                ", cars=" + cars +
                '}';
    }
} 