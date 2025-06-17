package com.arjunakankipati.racing_stat_analysis.model;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Represents a racing circuit (e.g., "Daytona International Speedway").
 * A circuit is a venue where racing events take place.
 */
public class Circuit implements BaseEntity<Long> {
    private Long id;
    private String name;
    private BigDecimal lengthMeters;
    private String country;
    private String location;
    private String description;

    /**
     * Default constructor.
     */
    public Circuit() {
    }

    /**
     * Full constructor.
     *
     * @param id the ID of the circuit
     * @param name the name of the circuit
     * @param lengthMeters the length of the circuit in meters
     * @param country the country where the circuit is located
     * @param location the specific location of the circuit
     * @param description the description of the circuit
     */
    public Circuit(Long id, String name, BigDecimal lengthMeters, String country, String location, String description) {
        this.id = id;
        this.name = name;
        this.lengthMeters = lengthMeters;
        this.country = country;
        this.location = location;
        this.description = description;
    }

    /**
     * Gets the ID of the circuit.
     *
     * @return the ID
     */
    @Override
    public Long getId() {
        return id;
    }

    /**
     * Sets the ID of the circuit.
     *
     * @param id the ID to set
     */
    @Override
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the name of the circuit.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the circuit.
     *
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the length of the circuit in meters.
     *
     * @return the length in meters
     */
    public BigDecimal getLengthMeters() {
        return lengthMeters;
    }

    /**
     * Sets the length of the circuit in meters.
     *
     * @param lengthMeters the length in meters to set
     */
    public void setLengthMeters(BigDecimal lengthMeters) {
        this.lengthMeters = lengthMeters;
    }

    /**
     * Gets the country where the circuit is located.
     *
     * @return the country
     */
    public String getCountry() {
        return country;
    }

    /**
     * Sets the country where the circuit is located.
     *
     * @param country the country to set
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * Gets the specific location of the circuit.
     *
     * @return the location
     */
    public String getLocation() {
        return location;
    }

    /**
     * Sets the specific location of the circuit.
     *
     * @param location the location to set
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Gets the description of the circuit.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the circuit.
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
        Circuit circuit = (Circuit) o;
        return Objects.equals(id, circuit.id) &&
                Objects.equals(name, circuit.name) &&
                Objects.equals(lengthMeters, circuit.lengthMeters) &&
                Objects.equals(country, circuit.country) &&
                Objects.equals(location, circuit.location) &&
                Objects.equals(description, circuit.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, lengthMeters, country, location, description);
    }

    @Override
    public String toString() {
        return "Circuit{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", lengthMeters=" + lengthMeters +
                ", country='" + country + '\'' +
                ", location='" + location + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}