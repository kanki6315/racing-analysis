package com.arjunakankipati.racingstatanalysis.dto;

import java.util.Objects;

/**
 * Data Transfer Object for Car Model information.
 */
public class CarModelDTO {
    private Long id;
    private String name;
    private String fullName;
    private Integer yearModel;
    private String description;

    /**
     * Default constructor.
     */
    public CarModelDTO() {
    }

    /**
     * Full constructor.
     *
     * @param id             the ID of the car model
     * @param name           the name of the car model (e.g., "911 GT3 R", "M4 GT3 EVO")
     * @param fullName       the full name of the car model (e.g., "Porsche 911 GT3 R")
     * @param yearModel      the year model of the car (e.g., 2023, 2024)
     * @param description    the description of the car model
     */
    public CarModelDTO(Long id, String name, String fullName, Integer yearModel, String description) {
        this.id = id;
        this.name = name;
        this.fullName = fullName;
        this.yearModel = yearModel;
        this.description = description;
    }

    /**
     * Gets the ID of the car model.
     *
     * @return the ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the ID of the car model.
     *
     * @param id the ID to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the name of the car model.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the car model.
     *
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the full name of the car model.
     *
     * @return the full name
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * Sets the full name of the car model.
     *
     * @param fullName the full name to set
     */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    /**
     * Gets the year model of the car.
     *
     * @return the year model
     */
    public Integer getYearModel() {
        return yearModel;
    }

    /**
     * Sets the year model of the car.
     *
     * @param yearModel the year model to set
     */
    public void setYearModel(Integer yearModel) {
        this.yearModel = yearModel;
    }

    /**
     * Gets the description of the car model.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the car model.
     *
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CarModelDTO that = (CarModelDTO) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(fullName, that.fullName) &&
                Objects.equals(yearModel, that.yearModel) &&
                Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, fullName, yearModel, description);
    }

    @Override
    public String toString() {
        return "CarModelDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", fullName='" + fullName + '\'' +
                ", yearModel=" + yearModel +
                ", description='" + description + '\'' +
                '}';
    }
} 