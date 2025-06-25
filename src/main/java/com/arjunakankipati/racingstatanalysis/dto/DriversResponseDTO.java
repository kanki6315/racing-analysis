package com.arjunakankipati.racingstatanalysis.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO for Drivers response information.
 */
public class DriversResponseDTO {
    private Long eventId;
    private String eventName;
    private List<DriverDTO> drivers;

    public DriversResponseDTO() {
        this.drivers = new ArrayList<>();
    }

    public DriversResponseDTO(Long eventId, String eventName) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.drivers = new ArrayList<>();
    }

    public DriversResponseDTO(Long eventId, String eventName, List<DriverDTO> drivers) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.drivers = drivers != null ? drivers : new ArrayList<>();
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public List<DriverDTO> getDrivers() {
        return drivers;
    }

    public void setDrivers(List<DriverDTO> drivers) {
        this.drivers = drivers != null ? drivers : new ArrayList<>();
    }

    public void addDriver(DriverDTO driver) {
        if (this.drivers == null) {
            this.drivers = new ArrayList<>();
        }
        this.drivers.add(driver);
    }
} 