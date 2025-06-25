package com.arjunakankipati.racingstatanalysis.dto;

import java.util.Objects;

/**
 * Data Transfer Object for Manufacturer information.
 */
public class ManufacturerDTO {
    private Long id;
    private String name;
    private String country;
    private String description;

    /**
     * Default constructor.
     */
    public ManufacturerDTO() {
    }

    /**
     * Full constructor.
     *
     * @param id          the ID of the manufacturer
     * @param name        the name of the manufacturer
     * @param country     the country of the manufacturer
     * @param description the description of the manufacturer
     */
    public ManufacturerDTO(Long id, String name, String country, String description) {
        this.id = id;
        this.name = name;
        this.country = country;
        this.description = description;
    }

    /**
     * Gets the ID of the manufacturer.
     *
     * @return the ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the ID of the manufacturer.
     *
     * @param id the ID to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the name of the manufacturer.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the manufacturer.
     *
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the country of the manufacturer.
     *
     * @return the country
     */
    public String getCountry() {
        return country;
    }

    /**
     * Sets the country of the manufacturer.
     *
     * @param country the country to set
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * Gets the description of the manufacturer.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the manufacturer.
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
        ManufacturerDTO that = (ManufacturerDTO) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(country, that.country) &&
                Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, country, description);
    }

    @Override
    public String toString() {
        return "ManufacturerDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", country='" + country + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
} 