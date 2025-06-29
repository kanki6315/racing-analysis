package com.arjunakankipati.racingstatanalysis.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Represents a racing event (e.g., "Rolex 24 at Daytona").
 * An event belongs to a series and takes place at a specific time and location.
 */
public class Event implements BaseEntity<Long> {
    private Long id;
    private Long seriesId;
    private Long circuitId;
    private String name;
    private Integer year;
    private LocalDate startDate;
    private LocalDate endDate;
    private String description;

    /**
     * Default constructor.
     */
    public Event() {
    }

    /**
     * Full constructor.
     *
     * @param id the ID of the event
     * @param seriesId the ID of the series this event belongs to
     * @param circuitId the ID of the circuit this event is held at
     * @param name the name of the event
     * @param year the year the event takes place
     * @param startDate the start date of the event
     * @param endDate the end date of the event
     * @param description the description of the event
     */
    public Event(Long id, Long seriesId, Long circuitId, String name, Integer year, LocalDate startDate, LocalDate endDate, String description) {
        this.id = id;
        this.seriesId = seriesId;
        this.circuitId = circuitId;
        this.name = name;
        this.year = year;
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
    }

    /**
     * Gets the ID of the event.
     *
     * @return the ID
     */
    @Override
    public Long getId() {
        return id;
    }

    /**
     * Sets the ID of the event.
     *
     * @param id the ID to set
     */
    @Override
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the ID of the series this event belongs to.
     *
     * @return the series ID
     */
    public Long getSeriesId() {
        return seriesId;
    }

    /**
     * Sets the ID of the series this event belongs to.
     *
     * @param seriesId the series ID to set
     */
    public void setSeriesId(Long seriesId) {
        this.seriesId = seriesId;
    }

    public Long getCircuitId() {
        return circuitId;
    }

    public void setCircuitId(Long circuitId) {
        this.circuitId = circuitId;
    }

    /**
     * Gets the name of the event.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the event.
     *
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the year the event takes place.
     *
     * @return the year
     */
    public Integer getYear() {
        return year;
    }

    /**
     * Sets the year the event takes place.
     *
     * @param year the year to set
     */
    public void setYear(Integer year) {
        this.year = year;
    }

    /**
     * Gets the start date of the event.
     *
     * @return the start date
     */
    public LocalDate getStartDate() {
        return startDate;
    }

    /**
     * Sets the start date of the event.
     *
     * @param startDate the start date to set
     */
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    /**
     * Gets the end date of the event.
     *
     * @return the end date
     */
    public LocalDate getEndDate() {
        return endDate;
    }

    /**
     * Sets the end date of the event.
     *
     * @param endDate the end date to set
     */
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    /**
     * Gets the description of the event.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the event.
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
        Event event = (Event) o;
        return Objects.equals(id, event.id) &&
                Objects.equals(seriesId, event.seriesId) &&
                Objects.equals(name, event.name) &&
                Objects.equals(year, event.year) &&
                Objects.equals(startDate, event.startDate) &&
                Objects.equals(endDate, event.endDate) &&
                Objects.equals(description, event.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, seriesId, name, year, startDate, endDate, description);
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", seriesId=" + seriesId +
                ", name='" + name + '\'' +
                ", year=" + year +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", description='" + description + '\'' +
                '}';
    }
}