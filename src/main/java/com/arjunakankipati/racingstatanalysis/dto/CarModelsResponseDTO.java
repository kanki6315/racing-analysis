package com.arjunakankipati.racingstatanalysis.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Response DTO for car models in a specific class for an event.
 */
public class CarModelsResponseDTO {
    private Long eventId;
    private String eventName;
    private Long classId;
    private String className;
    private List<CarModelDTO> carModels;

    /**
     * Default constructor.
     */
    public CarModelsResponseDTO() {
        this.carModels = new ArrayList<>();
    }

    /**
     * Constructor with event and class information.
     *
     * @param eventId   the ID of the event
     * @param eventName the name of the event
     * @param classId   the ID of the class
     * @param className the name of the class
     */
    public CarModelsResponseDTO(Long eventId, String eventName, Long classId, String className) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.classId = classId;
        this.className = className;
        this.carModels = new ArrayList<>();
    }

    /**
     * Constructor with event, class information and car models.
     *
     * @param eventId   the ID of the event
     * @param eventName the name of the event
     * @param classId   the ID of the class
     * @param className the name of the class
     * @param carModels the list of car models
     */
    public CarModelsResponseDTO(Long eventId, String eventName, Long classId, String className, List<CarModelDTO> carModels) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.classId = classId;
        this.className = className;
        this.carModels = carModels != null ? carModels : new ArrayList<>();
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
     * Gets the list of car models.
     *
     * @return the car models
     */
    public List<CarModelDTO> getCarModels() {
        return carModels;
    }

    /**
     * Sets the list of car models.
     *
     * @param carModels the car models to set
     */
    public void setCarModels(List<CarModelDTO> carModels) {
        this.carModels = carModels != null ? carModels : new ArrayList<>();
    }

    /**
     * Adds a car model to the list.
     *
     * @param carModelDTO the car model to add
     */
    public void addCarModel(CarModelDTO carModelDTO) {
        if (this.carModels == null) {
            this.carModels = new ArrayList<>();
        }
        this.carModels.add(carModelDTO);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CarModelsResponseDTO that = (CarModelsResponseDTO) o;
        return Objects.equals(eventId, that.eventId) &&
                Objects.equals(eventName, that.eventName) &&
                Objects.equals(classId, that.classId) &&
                Objects.equals(className, that.className) &&
                Objects.equals(carModels, that.carModels);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId, eventName, classId, className, carModels);
    }

    @Override
    public String toString() {
        return "CarModelsResponseDTO{" +
                "eventId=" + eventId +
                ", eventName='" + eventName + '\'' +
                ", classId=" + classId +
                ", className='" + className + '\'' +
                ", carModels=" + carModels +
                '}';
    }
} 