package com.arjunakankipati.racingstatanalysis.dto;

/**
 * DTO for creating or updating a Series.
 */
public class SeriesDTO {
    private String name;
    private String description;

    public SeriesDTO() {
    }

    public SeriesDTO(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
} 