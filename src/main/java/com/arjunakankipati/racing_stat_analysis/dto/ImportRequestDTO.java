package com.arjunakankipati.racing_stat_analysis.dto;

/**
 * Data Transfer Object for import requests.
 * Contains the URL of the JSON file to import and metadata about the import.
 */
public class ImportRequestDTO {
    private String url;
    private String seriesName;
    private String eventName;
    private Integer year;

    /**
     * Default constructor.
     */
    public ImportRequestDTO() {
    }

    /**
     * Full constructor.
     *
     * @param url the URL of the JSON file to import
     * @param seriesName the name of the series
     * @param eventName the name of the event
     * @param year the year of the event
     */
    public ImportRequestDTO(String url, String seriesName, String eventName, Integer year) {
        this.url = url;
        this.seriesName = seriesName;
        this.eventName = eventName;
        this.year = year;
    }

    /**
     * Gets the URL of the JSON file to import.
     *
     * @return the URL
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets the URL of the JSON file to import.
     *
     * @param url the URL to set
     */
    public void setUrl(String url) {
        this.url = url;
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
     * Gets the year of the event.
     *
     * @return the year
     */
    public Integer getYear() {
        return year;
    }

    /**
     * Sets the year of the event.
     *
     * @param year the year to set
     */
    public void setYear(Integer year) {
        this.year = year;
    }
}