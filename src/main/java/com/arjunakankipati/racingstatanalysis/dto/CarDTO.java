package com.arjunakankipati.racingstatanalysis.dto;

import java.util.List;
import java.util.ArrayList;

/**
 * DTO for Car information including its drivers.
 */
public class CarDTO {
    private Long carId;
    private String number;
    private String model;
    private String tireSupplier;
    private Long classId;
    private Long manufacturerId;
    private List<DriverDTO> drivers;

    public CarDTO() {
        this.drivers = new ArrayList<>();
    }

    public CarDTO(Long carId, String number, String model, String tireSupplier,
                  Long classId, Long manufacturerId) {
        this.carId = carId;
        this.number = number;
        this.model = model;
        this.tireSupplier = tireSupplier;
        this.classId = classId;
        this.manufacturerId = manufacturerId;
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

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
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

    public Long getManufacturerId() {
        return manufacturerId;
    }

    public void setManufacturerId(Long manufacturerId) {
        this.manufacturerId = manufacturerId;
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