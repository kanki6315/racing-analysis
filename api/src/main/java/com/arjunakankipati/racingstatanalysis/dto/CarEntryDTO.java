package com.arjunakankipati.racingstatanalysis.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO for Car Entry information including its car model and drivers.
 * Represents an individual car entry in a racing session.
 */
public class CarEntryDTO {
    private Long carId;
    private String number;
    private CarModelDTO carModel;
    private String tireSupplier;
    private Long classId;
    private Long teamId;
    private String teamName;
    private List<DriverDTO> drivers;

    public CarEntryDTO() {
        this.drivers = new ArrayList<>();
    }

    public CarEntryDTO(Long carId, String number, CarModelDTO carModel, String tireSupplier,
                       Long classId, Long teamId, String teamName) {
        this.carId = carId;
        this.number = number;
        this.carModel = carModel;
        this.tireSupplier = tireSupplier;
        this.classId = classId;
        this.teamId = teamId;
        this.teamName = teamName;
        this.drivers = new ArrayList<>();
    }

    public Long getCarId() {
        return carId;
    }

    public void setCarId(Long carId) {
        this.carId = carId;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public CarModelDTO getCarModel() {
        return carModel;
    }

    public void setCarModel(CarModelDTO carModel) {
        this.carModel = carModel;
    }

    public String getTireSupplier() {
        return tireSupplier;
    }

    public void setTireSupplier(String tireSupplier) {
        this.tireSupplier = tireSupplier;
    }

    public Long getClassId() {
        return classId;
    }

    public void setClassId(Long classId) {
        this.classId = classId;
    }

    public Long getTeamId() {
        return teamId;
    }

    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public List<DriverDTO> getDrivers() {
        return drivers;
    }

    public void setDrivers(List<DriverDTO> drivers) {
        this.drivers = drivers;
    }

    public void addDriver(DriverDTO driver) {
        if (this.drivers == null) {
            this.drivers = new ArrayList<>();
        }
        this.drivers.add(driver);
    }
}