package com.arjunakankipati.racingstatanalysis.dto;

import java.time.LocalDate;

public class EventDTO {

    private Long eventId;
    private Long seriesId;
    private String name;
    private Integer year;
    private LocalDate startDate;
    private LocalDate endDate;
    private String description;

    public EventDTO() {
    }

    public EventDTO(Long seriesId, String name, Integer year, LocalDate startDate, LocalDate endDate, String description) {
        this.seriesId = seriesId;
        this.name = name;
        this.year = year;
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
    }

    public EventDTO(Long eventId, Long seriesId, String name, Integer year, LocalDate startDate, LocalDate endDate, String description) {
        this(seriesId, name, year, startDate, endDate, description);
        this.eventId = eventId;
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
