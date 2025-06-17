package com.arjunakankipati.racingstatanalysis.dto;

/**
 * Data Transfer Object for import requests.
 * Contains the URL of the JSON file to import.
 */
public class ImportRequestDTO {
    private String url;

    /**
     * Default constructor.
     */
    public ImportRequestDTO() {
    }

    /**
     * Constructor with URL.
     *
     * @param url the URL of the JSON file to import
     */
    public ImportRequestDTO(String url) {
        this.url = url;
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
}
