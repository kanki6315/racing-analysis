package com.arjunakankipati.racingstatanalysis.model;

import java.util.Objects;

/**
 * Represents a racing series (e.g., "IMSA WeatherTech SportsCar Championship").
 * This is a core entity in the racing timing data analysis system.
 */
public class Series implements BaseEntity<Long> {
    private Long id;
    private String name;
    private String description;

    /**
     * Default constructor.
     */
    public Series() {
    }

    /**
     * Full constructor.
     *
     * @param id the ID of the series
     * @param name the name of the series
     * @param description the description of the series
     */
    public Series(Long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    /**
     * Gets the ID of the series.
     *
     * @return the ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the ID of the series.
     *
     * @param id the ID to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the name of the series.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the series.
     *
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the description of the series.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the series.
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
        Series series = (Series) o;
        return Objects.equals(id, series.id) &&
               Objects.equals(name, series.name) &&
               Objects.equals(description, series.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description);
    }

    @Override
    public String toString() {
        return "Series{" +
               "id=" + id +
               ", name='" + name + '\'' +
               ", description='" + description + '\'' +
               '}';
    }
}
