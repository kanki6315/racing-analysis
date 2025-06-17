package com.arjunakankipati.racing_stat_analysis.model;

import java.util.Objects;

/**
 * Represents a car manufacturer (e.g., "BMW").
 * A manufacturer produces cars that participate in racing events.
 */
public class Manufacturer implements BaseEntity<Long> {
    private Long id;
    private String name;
    private String country;

    /**
     * Default constructor.
     */
    public Manufacturer() {
    }

    /**
     * Full constructor.
     *
     * @param id the ID of the manufacturer
     * @param name the name of the manufacturer
     * @param country the country of origin of the manufacturer
     */
    public Manufacturer(Long id, String name, String country) {
        this.id = id;
        this.name = name;
        this.country = country;
    }

    /**
     * Gets the ID of the manufacturer.
     *
     * @return the ID
     */
    @Override
    public Long getId() {
        return id;
    }

    /**
     * Sets the ID of the manufacturer.
     *
     * @param id the ID to set
     */
    @Override
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
     * Gets the country of origin of the manufacturer.
     *
     * @return the country
     */
    public String getCountry() {
        return country;
    }

    /**
     * Sets the country of origin of the manufacturer.
     *
     * @param country the country to set
     */
    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Manufacturer manufacturer = (Manufacturer) o;
        return Objects.equals(id, manufacturer.id) &&
                Objects.equals(name, manufacturer.name) &&
                Objects.equals(country, manufacturer.country);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, country);
    }

    @Override
    public String toString() {
        return "Manufacturer{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", country='" + country + '\'' +
                '}';
    }
}