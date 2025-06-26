package com.arjunakankipati.racingstatanalysis.dto;

import java.util.List;

/**
 * Data Transfer Object for series responses.
 * Contains information about a racing series.
 */
public class SeriesResponseDTO {
    private Long id;
    private String name;
    private Integer eventCount;
    private List<Integer> years;

    /**
     * Default constructor.
     */
    public SeriesResponseDTO() {
    }

    /**
     * Full constructor.
     *
     * @param id the ID of the series
     * @param name the name of the series
     * @param eventCount the number of events in the series
     * @param years the list of years for the series
     */
    public SeriesResponseDTO(Long id, String name, Integer eventCount, List<Integer> years) {
        this.id = id;
        this.name = name;
        this.eventCount = eventCount;
        this.years = years;
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
     * Gets the number of events in the series.
     *
     * @return the event count
     */
    public Integer getEventCount() {
        return eventCount;
    }

    /**
     * Sets the number of events in the series.
     *
     * @param eventCount the event count to set
     */
    public void setEventCount(Integer eventCount) {
        this.eventCount = eventCount;
    }

    /**
     * Gets the list of years for the series.
     *
     * @return the list of years
     */
    public List<Integer> getYears() {
        return years;
    }

    /**
     * Sets the list of years for the series.
     *
     * @param years the list of years to set
     */
    public void setYears(List<Integer> years) {
        this.years = years;
    }
}