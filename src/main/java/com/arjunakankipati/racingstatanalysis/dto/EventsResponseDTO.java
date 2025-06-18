package com.arjunakankipati.racingstatanalysis.dto;

import java.time.LocalDate;

public class EventsResponseDTO {

    private Long eventId;
    private Long seriesId;
    private String name;
    private Integer year;
    private LocalDate startDate;
    private LocalDate endDate;
    private String description;

    public EventsResponseDTO() {
    }

    public EventsResponseDTO(Long eventId, Long seriesId, String name, Integer year, LocalDate startDate, LocalDate endDate, String description) {
        this.eventId = eventId;
        this.seriesId = seriesId;
        this.name = name;
        this.year = year;
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Long getSeriesId() {
        return seriesId;
    }

    public void setSeriesId(Long seriesId) {
        this.seriesId = seriesId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
