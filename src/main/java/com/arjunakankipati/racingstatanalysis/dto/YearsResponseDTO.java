package com.arjunakankipati.racingstatanalysis.dto;

import java.util.List;

/**
 * Data Transfer Object for years responses.
 * Contains a list of years for a specific series.
 */
public class YearsResponseDTO {
    private Long seriesId;
    private String seriesName;
    private List<Integer> years;

    /**
     * Default constructor.
     */
    public YearsResponseDTO() {
    }

    /**
     * Full constructor.
     *
     * @param seriesId the ID of the series
     * @param seriesName the name of the series
     * @param years the list of years
     */
    public YearsResponseDTO(Long seriesId, String seriesName, List<Integer> years) {
        this.seriesId = seriesId;
        this.seriesName = seriesName;
        this.years = years;
    }

    /**
     * Gets the ID of the series.
     *
     * @return the series ID
     */
    public Long getSeriesId() {
        return seriesId;
    }

    /**
     * Sets the ID of the series.
     *
     * @param seriesId the series ID to set
     */
    public void setSeriesId(Long seriesId) {
        this.seriesId = seriesId;
    }

    /**
     * Gets the name of the series.
     *
     * @return the series name
     */
    public String getSeriesName() {
        return seriesName;
    }

    /**
     * Sets the name of the series.
     *
     * @param seriesName the series name to set
     */
    public void setSeriesName(String seriesName) {
        this.seriesName = seriesName;
    }

    /**
     * Gets the list of years.
     *
     * @return the years
     */
    public List<Integer> getYears() {
        return years;
    }

    /**
     * Sets the list of years.
     *
     * @param years the years to set
     */
    public void setYears(List<Integer> years) {
        this.years = years;
    }
}