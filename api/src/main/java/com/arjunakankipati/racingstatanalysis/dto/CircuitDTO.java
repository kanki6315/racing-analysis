package com.arjunakankipati.racingstatanalysis.dto;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Data Transfer Object for Circuit information.
 */
public class CircuitDTO {
    private Long id;
    private String name;
    private BigDecimal lengthMeters;
    private String country;
    private String location;
    private String description;

    public CircuitDTO() {
    }

    public CircuitDTO(Long id, String name, BigDecimal lengthMeters, String country, String location, String description) {
        this.id = id;
        this.name = name;
        this.lengthMeters = lengthMeters;
        this.country = country;
        this.location = location;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getLengthMeters() {
        return lengthMeters;
    }

    public void setLengthMeters(BigDecimal lengthMeters) {
        this.lengthMeters = lengthMeters;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CircuitDTO that = (CircuitDTO) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(lengthMeters, that.lengthMeters) &&
                Objects.equals(country, that.country) &&
                Objects.equals(location, that.location) &&
                Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, lengthMeters, country, location, description);
    }

    @Override
    public String toString() {
        return "CircuitDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", lengthMeters=" + lengthMeters +
                ", country='" + country + '\'' +
                ", location='" + location + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
} 