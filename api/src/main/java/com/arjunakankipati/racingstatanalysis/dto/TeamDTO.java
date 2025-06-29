package com.arjunakankipati.racingstatanalysis.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO for Team information including its cars and drivers.
 */
public class TeamDTO {
    private Long teamId;
    private String name;
    private String description;
    private List<CarEntryDTO> cars;

    public TeamDTO() {
        this.cars = new ArrayList<>();
    }

    public TeamDTO(Long teamId, String name, String description) {
        this.teamId = teamId;
        this.name = name;
        this.description = description;
        this.cars = new ArrayList<>();
    }

    public Long getTeamId() {
        return teamId;
    }

    public void setTeamId(Long teamId) {
        this.teamId = teamId;
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

    public List<CarEntryDTO> getCars() {
        return cars;
    }

    public void setCars(List<CarEntryDTO> cars) {
        this.cars = cars;
    }

    public void addCar(CarEntryDTO car) {
        if (this.cars == null) {
            this.cars = new ArrayList<>();
        }
        this.cars.add(car);
    }
}