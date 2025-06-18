package com.arjunakankipati.racingstatanalysis.dto;

import java.util.List;
import java.util.ArrayList;

/**
 * DTO for Team information including its cars and drivers.
 */
public class TeamDTO {
    private Long teamId;
    private String name;
    private String description;
    private List<CarDTO> cars;

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

    public List<CarDTO> getCars() {
        return cars;
    }

    public void setCars(List<CarDTO> cars) {
        this.cars = cars;
    }

    public void addCar(CarDTO car) {
        if (this.cars == null) {
            this.cars = new ArrayList<>();
        }
        this.cars.add(car);
    }
}